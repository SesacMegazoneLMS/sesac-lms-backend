package com.sesac.backend.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.orders.constants.OrderStatus;
import com.sesac.backend.orders.domain.Order;
import com.sesac.backend.orders.domain.OrderedCourses;
import com.sesac.backend.orders.repository.OrderRepository;
import com.sesac.backend.payments.constants.PaymentStatus;
import com.sesac.backend.payments.domain.Payment;
import com.sesac.backend.payments.dto.request.PortOnePayment;
import com.sesac.backend.payments.dto.response.PaymentResponse;
import com.sesac.backend.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final RestTemplate restTemplate;

    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
    private String apiSecret;


    @Transactional
    public void verifyPayment(String impUid, String merchantUid) {

        try {
            // 주문 조회
            Order order = orderRepository.findByMerchantUid(merchantUid)
                    .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다"));

            // 이미 결제 정보가 있는지 확인
            Optional<Payment> existingPayment = paymentRepository.findByOrder(order);
            if (existingPayment.isPresent()) {
                return;
            }

            // 포트원 API로 결제 정보 조회
            String token = getPortOneAccessToken();
            PaymentResponse portOnePayment = getPaymentInfo(impUid, token);

            // 결제 정보 저장 (verify 성공 여부와 관계 없이)
            Payment payment = Payment.builder()
                    .order(order)
                    .impUid(impUid)
                    .amount(order.getTotalAmount())
                    .build();

            // verify 실피 시 READY 상태로 저장
            if (portOnePayment == null || portOnePayment.getResponse() == null) {
                payment.setPaymentStatus(PaymentStatus.READY);
                paymentRepository.save(payment);
                return;
            }

            // 검증 로직
            PortOnePayment portOneResponse = portOnePayment.getResponse();

            // 1. 결제 금액 검증
            if (!order.getTotalAmount().equals(portOneResponse.getAmount())) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new RuntimeException("결제 금액이 일치하지 않습니다. 주문금액: " +
                        order.getTotalAmount() + ", 실제결제금액: " + portOneResponse.getAmount());
            }

            // 2. 결제 상태 검증
            if (!"paid".equals(portOneResponse.getStatus())) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new RuntimeException("결제가 완료되지 않았습니다. 상태: " + portOneResponse.getStatus());
            }

            // 3. 위변조 검증(merchant_uid가 해당 주문의 것이 맞는지)
            if (!merchantUid.equals(portOneResponse.getMerchant_uid())) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new RuntimeException("주문정보가 위변조되었습니다.");
            }

            // 모든 검증 통과 시 결제 정보 업데이트
            payment.setPaymentStatus(PaymentStatus.READY);
            payment.setMethod(portOneResponse.getPay_method());
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // 주문 상태 업데이트
            order.setOrderStatus(OrderStatus.PAID);
            createEnrolledCourse(order);

        } catch (DataIntegrityViolationException e) { // DB 제약 조건 위반 시
            log.warn("Concurrent payment creation detected for order: {}", merchantUid);
            // 이미 다른 트랜잭션에서 결제가 생성됨
            return;
        } catch (Exception e) {
            throw e;
        }

    }

    private String getPortOneAccessToken() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("imp_key", apiKey);
        requestMap.put("imp_secret", apiSecret);

        try {
            String requestBody = objectMapper.writeValueAsString(requestMap);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.iamport.kr/users/getToken",
                    new HttpEntity<>(requestBody, headers),
                    Map.class
            );

            return ((Map<String, String>)response.getBody().get("response")).get("access_token");

        } catch (Exception e) {
            throw new RuntimeException("PortOne Access Token을 가져오는데 실패했습니다: " + e.getMessage());
        }
    }

    private PaymentResponse getPaymentInfo(String impUid, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<PaymentResponse> response = restTemplate.exchange(
                "https://api.iamport.kr/payments/" + impUid,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PaymentResponse.class
        );

        return response.getBody();
    }

    private void createEnrolledCourse(Order order) {

        for (OrderedCourses orderedCourses : order.getOrderedCourses()) {
            // 이미 등록된 수강 정보가 있는지 확인
            if (enrollmentRepository.existsByOrderedCourses(orderedCourses)) {
                continue;
            }

            Enrollment enrollment = Enrollment.builder()
                    .user(order.getUser())
                    .orderedCourses(orderedCourses)
                    .isActive(true)
                    .build();

            enrollmentRepository.save(enrollment);

        }
    }

    @Transactional
    public void processWebHook(Map<String, String> webHookData) {

        String impUid = webHookData.get("imp_uid");
        String merchantUid = webHookData.get("merchant_uid");
        String status = webHookData.get("status");

        try {
            // WebHook이 verify보다 먼저 도착할 수 있음
            // 따라서 findByImpUid가 실패할 수 있음
            // 결론적으로, payment는 findByImpUid로 찾은 기존 객체이거나(verify가 먼저 도착하여 DB에 이미 값이 있는 경우), orElseGet()에서 새로 생성한 객체를 참조함
            Payment payment = paymentRepository.findByImpUid(impUid)
                    // findByImpUid가 비어있을 경우(WebHook이 verify보다 먼저 도착) 새로운 Payment 객체 생성
                    .orElseGet(() -> {
                        Order order = orderRepository.findByMerchantUid(merchantUid)
                                .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다"));

                        Payment paymentFromWebHook = Payment.builder()
                                .order(order)
                                .impUid(impUid)
                                .amount(order.getTotalAmount())
                                .paymentStatus(PaymentStatus.READY)
                                .build();

                        return paymentRepository.save(paymentFromWebHook);
                    });

            // 이미 처리된 상태면 중복 처리 방지
            if (payment.getPaymentStatus() == PaymentStatus.PAID && "paid".equals(status)) {
                return;
            }

            switch (status) {
                case "paid" -> handlePaymentSuccess(payment);
                case "cancelled" -> handlePaymentCancellation(payment);
                case "failed" -> handlePaymentFailure(payment);
            }

            paymentRepository.save(payment);

        } catch (Exception e) {
            throw new RuntimeException("WebHook 진행 실패", e);
        }
    }

    private void handlePaymentSuccess(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
    }

    private void handlePaymentCancellation(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        payment.setRefundedAt(LocalDateTime.now());

        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.CANCELED);

        // 수강 정보 비활성화
        for (OrderedCourses orderedCourses : order.getOrderedCourses()) {
            Enrollment enrollment = enrollmentRepository.findByOrderedCourses(orderedCourses)
                    .orElseThrow(() -> new RuntimeException("수강 정보를 찾을 수 없습니다"));
            enrollment.setActive(false);
        }
    }

    private void handlePaymentFailure(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.FAILED);
    }
}
