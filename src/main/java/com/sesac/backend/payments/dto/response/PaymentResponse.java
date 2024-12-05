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

    private Integer code;

    private String message;

    private PortOnePayment response;
}
