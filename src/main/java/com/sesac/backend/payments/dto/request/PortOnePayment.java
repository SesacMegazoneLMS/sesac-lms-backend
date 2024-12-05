package com.sesac.backend.payments.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortOnePayment {

    private String imp_uid;

    private String merchant_uid;

    private String status;

    private String name;

    private Integer amount;

    private String pay_method;
}
