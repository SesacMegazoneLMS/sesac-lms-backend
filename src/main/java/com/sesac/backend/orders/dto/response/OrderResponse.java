package com.sesac.backend.orders.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {

    private String nickname;

    private String merchantUid;

    private Integer totalAmount;
}
