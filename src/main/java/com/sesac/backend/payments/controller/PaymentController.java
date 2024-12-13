package com.sesac.backend.payments.controller;

import com.sesac.backend.payments.annotation.PortOneIpOnly;
import com.sesac.backend.payments.dto.request.PaymentVerification;
import com.sesac.backend.payments.dto.response.PortOnePaymentWebHookResponse;
import com.sesac.backend.payments.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequestMapping("/payments")
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerification payload) {

        try {
            paymentService.verifyPayment(payload.getImpUid(), payload.getMerchantUid());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "결제 정보가 저장되었습니다"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK) // 실패해도 200 반환
                    .body(Map.of(
                            "status", "pending",
                            "message", "결제는 완료되었으나 검증에 실패했습니다. 잠시 후 자동으로 처리됩니다."
                    ));
        }

    }

    @PostMapping("/webhook")
    @PortOneIpOnly
    public ResponseEntity<?> handleWebHook(HttpServletRequest request, @RequestBody PortOnePaymentWebHookResponse webHookData) {

        // IP 로깅
        log.info("📌 Webhook called from IP: {}", request.getRemoteAddr());
        log.info("📌 Webhook X-Forwarded-For: {}", request.getHeader("X-Forwarded-For"));
        log.info("📌 Webhook Data: {}", webHookData);

        try {
            paymentService.processWebHook(webHookData);
            return ResponseEntity.ok(Map.of(
                    "message", "WebHook 연결이 성공적으로 진행되었습니다"
            ));

        } catch (Exception e) {
            log.error("❌ Webhook processing error", e);  // 에러 로깅
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "WebHook 응답은 받았으나 연결에 실패하였습니다",
                    "error", e.getMessage()
            ));
        }
    }
}
