package com.sesac.backend.orders.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderedCourseInfoForRequest {

    private Long courseId;

    private Integer price;
}
