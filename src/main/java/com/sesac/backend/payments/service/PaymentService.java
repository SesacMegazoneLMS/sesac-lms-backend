package com.sesac.backend.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesac.backend.enrollments.domain.Enrollment;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.orders.constants.OrderStatus;
import com.sesac.backend.orders.constants.OrderedCoursesStatus;
import com.sesac.backend.orders.domain.Order;
import com.sesac.backend.orders.domain.OrderedCourses;
import com.sesac.backend.orders.repository.OrderRepository;
import com.sesac.backend.payments.constants.PaymentStatus;
import com.sesac.backend.payments.domain.Payment;
import com.sesac.backend.payments.dto.response.PortOnePaymentApiResponse;
import com.sesac.backend.payments.dto.response.PortOnePaymentApiResponseInfo;
import com.sesac.backend.payments.dto.response.PortOnePaymentWebHookResponse;
import com.sesac.backend.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final ObjectMapper objectMapper;

    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
    private String apiSecret;


    @Transactional
    public void verifyPayment(String impUid, String merchantUid) {

        try {
            // 1. 주문 조회
            Order order = orderRepository.findByMerchantUid(merchantUid)
                    .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다"));

            // 2. 이미 처리된 결제인지 확인
            Optional<Payment> existingPayment = paymentRepository.findByImpUid(impUid);
            if (existingPayment.isPresent()) {
                Payment payment = existingPayment.get();
                if (payment.getPaymentStatus() == PaymentStatus.PAID) {
                    log.info("Payment already verified for impUid: {}", impUid);
                    return;
                }
            }

            // 3. 포트원 API로 결제 정보 조회
            String token = getPortOneAccessToken();
            PortOnePaymentApiResponse portOnePayment = getPaymentInfo(impUid, token);

            if (portOnePayment == null) {
                throw new RuntimeException("결제 정보를 조회할 수 없습니다.");
            }

            // 4. API 검증
            validatePayment(order, portOnePayment.getResponse());

            // 5. 결제 정보 생성 (PENDING 상태로)
            Payment payment = existingPayment.orElseGet(() -> Payment.builder()
                    .order(order)
                    .impUid(impUid)
                    .amount(order.getTotalAmount())
                    .build());

            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setMethod(portOnePayment.getResponse().getPay_method());
            paymentRepository.save(payment);

        } catch (Exception e) {
            log.error("Payment API verification failed", e);
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

            return ((Map<String, String>) response.getBody().get("response")).get("access_token");

        } catch (Exception e) {
            throw new RuntimeException("PortOne Access Token을 가져오는데 실패했습니다: " + e.getMessage());
        }
    }

    private PortOnePaymentApiResponse getPaymentInfo(String impUid, String token) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            // PortOne에서 제공하는 결제 내역 API를 통하여 내역 조회

            ResponseEntity<PortOnePaymentApiResponse> response = restTemplate.exchange(
                    "https://api.iamport.kr/payments/" + impUid, // PortOne 결제 내역 API 요청 주소
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    PortOnePaymentApiResponse.class
            );

            log.info("💰 Webhook amount In getPaymentInfo: {}", response);

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("포트원 결제 정보 조회 실패", e);
        }
    }

    private void validatePayment(Order order, PortOnePaymentApiResponseInfo portOneResponse) {

        if (!order.getTotalAmount().equals(portOneResponse.getAmount())) {
            throw new RuntimeException("결제 금액이 일치하지 않습니다. 주문금액: " +
                    order.getTotalAmount() + ", 실제결제금액: " + portOneResponse.getAmount());
        }

        if (!"paid".equals(portOneResponse.getStatus())) {
            throw new RuntimeException("결제가 완료되지 않았습니다. 상태: " + portOneResponse.getStatus());
        }

        if (!order.getMerchantUid().equals(portOneResponse.getMerchant_uid())) {
            throw new RuntimeException("주문정보가 위변조되었습니다.");
        }

    }

    @Transactional
    public void createEnrolledCourse(Order order) {

        for (OrderedCourses orderedCourses : order.getOrderedCourses()) {
            // 이미 등록된 수강 정보가 있는지 확인
            boolean exists = enrollmentRepository.existsByOrderedCourses(orderedCourses);
            if (!exists) {
                Enrollment enrollment = Enrollment.builder()
                        .user(order.getUser())
                        .orderedCourses(orderedCourses)
                        .isActive(true)
                        .build();
                enrollmentRepository.save(enrollment);
            }
        }
    }

    @Transactional
    public void processWebHook(PortOnePaymentWebHookResponse webHookData) {

        log.info("💡 Webhook data In processWebHook: {}", webHookData);

        try {
            Payment payment = paymentRepository.findByImpUid(webHookData.getImp_uid())
                    .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다"));

            // 이미 완료된 결제인지 확인
            if (payment.getPaymentStatus() == PaymentStatus.PAID) {
                log.info("Payment already completed for impUid: {}", webHookData.getImp_uid());
                return;
            }

            log.info("💰 Log before validateWebhook");

            // 웹훅 데이터 검증
            validateWebhookData(payment, webHookData);

            // 결제 상태에 따른 처리
            switch (webHookData.getStatus()) {
                case "paid" -> {
                    handlePaymentSuccess(payment);
                    Order order = payment.getOrder();
                    order.getOrderedCourses().forEach(orderedCourse -> {
                        orderedCourse.setStatus(OrderedCoursesStatus.ACTIVE);
                    });
                    log.info("💡 OrderedCourses status updated to ACTIVE for order: {}", order.getId());
                }
                case "cancelled" -> handlePaymentCancellation(payment);
                case "failed" -> handlePaymentFailure(payment);
                default -> throw new RuntimeException("Unknown payment status: " + webHookData.getStatus());
            }

            paymentRepository.save(payment);

        } catch (Exception e) {
            log.error("Webhook verification failed", e);
            throw e;
        }

//        String impUid = webHookData.get("imp_uid");
//        String merchantUid = webHookData.get("merchant_uid");
//        String status = webHookData.get("status");
//
//        try {
//            // WebHook이 verify보다 먼저 도착할 수 있음
//            // 따라서 findByImpUid가 실패할 수 있음
//            // 결론적으로, payment는 findByImpUid로 찾은 기존 객체이거나(verify가 먼저 도착하여 DB에 이미 값이 있는 경우), orElseGet()에서 새로 생성한 객체를 참조함
//            Payment payment = paymentRepository.findByImpUid(impUid)
//                    // findByImpUid가 비어있을 경우(WebHook이 verify보다 먼저 도착) 새로운 Payment 객체 생성
//                    .orElseGet(() -> {
//                        Order order = orderRepository.findByMerchantUid(merchantUid)
//                                .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다"));
//
//                        Payment paymentFromWebHook = Payment.builder()
//                                .order(order)
//                                .impUid(impUid)
//                                .amount(order.getTotalAmount())
//                                .paymentStatus(PaymentStatus.READY)
//                                .build();
//
//                        return paymentRepository.save(paymentFromWebHook);
//                    });
//
//            // 이미 처리된 상태면 중복 처리 방지
//            if (payment.getPaymentStatus() == PaymentStatus.PAID && "paid".equals(status)) {
//                return;
//            }
//
//            switch (status) {
//                case "paid" -> handlePaymentSuccess(payment);
//                case "cancelled" -> handlePaymentCancellation(payment);
//                case "failed" -> handlePaymentFailure(payment);
//            }
//
//            paymentRepository.save(payment);
//
//        } catch (Exception e) {
//            throw new RuntimeException("WebHook 진행 실패", e);
//        }
    }

    private void validateWebhookData(Payment payment, PortOnePaymentWebHookResponse webhookData) {
        log.info("💡 Webhook validation - imp_uid: {}, merchant_uid: {}, status: {}",
                webhookData.getImp_uid(),
                webhookData.getMerchant_uid(),
                webhookData.getStatus());

        // 1. merchant_uid 검증 (주문 정보 일치 여부)
        if (!payment.getOrder().getMerchantUid().equals(webhookData.getMerchant_uid())) {
            throw new RuntimeException("주문정보가 일치하지 않습니다");
        }

        // 2. imp_uid 검증 (결제 정보 일치 여부)
        if (!payment.getImpUid().equals(webhookData.getImp_uid())) {
            throw new RuntimeException("결제정보가 일치하지 않습니다");
        }

        // 3. status 검증 (결제 상태)
        if (!"paid".equals(webhookData.getStatus())) {
            throw new RuntimeException("결제가 완료되지 않았습니다. 상태: " + webhookData.getStatus());
        }
    }

    private void handlePaymentSuccess(Payment payment) {

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.PAID);

        createEnrolledCourse(order);
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
