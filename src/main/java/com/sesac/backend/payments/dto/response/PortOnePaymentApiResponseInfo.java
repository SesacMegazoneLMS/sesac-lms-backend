package com.sesac.backend.payments.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PortOnePaymentApiResponseInfo {

    private String imp_uid;

    private String merchant_uid;

    private String pay_method;

    private String name;

    private Integer amount;

    private String buyer_name;

    private String status;
}
