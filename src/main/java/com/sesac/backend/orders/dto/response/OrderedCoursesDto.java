package com.sesac.backend.orders.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderedCoursesDto {

    private Long id;

    private Long courseId;

    private Long orderId;

    private Integer price; // 구매 당시 가격

    private String status;
}