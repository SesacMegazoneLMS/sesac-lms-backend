package com.sesac.backend.payments.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortOnePaymentWebHookResponse {

    private String imp_uid;

    private String merchant_uid;

    private String status;

    private String cancellation_id;
}
