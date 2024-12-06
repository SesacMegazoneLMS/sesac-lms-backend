package com.sesac.backend.carts.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartRequest {
    // course에 대한 정보 조회를 위한 course pk
    private Long courseId;
}
