package com.sesac.backend.payments.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentVerification {

    private String impUid;

    private String merchantUid;

    private String buyerName;

    private Integer amount;

    private String status;

    private String payMethod;

}
