package com.sesac.backend.orders.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderRequest {

    private List<OrderedCourseInfoForRequest> courses;

    private Integer totalAmount;
}
