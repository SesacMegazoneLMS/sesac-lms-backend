package com.sesac.backend.payments.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortOnePaymentApiResponse {

    private Integer code;
    private String message;
    private PortOnePaymentApiResponseInfo response;

}
