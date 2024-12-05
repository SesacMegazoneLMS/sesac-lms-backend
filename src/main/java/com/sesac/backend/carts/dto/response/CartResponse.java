package com.sesac.backend.carts.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartResponse {

    //private Long id;

    // JSON 형태로 장바구니 아이템 정보를 보냄
    private JsonNode cartInfo;
}
