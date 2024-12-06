package com.sesac.backend.payments.dto.response;

import com.sesac.backend.payments.dto.request.PortOnePayment;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentResponse {

    // 포트원이 제공하는 결제 내역 조회 api

    private Integer code; // 응답 코드

    private String message; // 응답 메세지

    private PortOnePayment response; // PaymentAnnotation
}
