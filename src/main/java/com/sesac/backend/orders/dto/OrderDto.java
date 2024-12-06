package com.sesac.backend.orders.dto;

import com.sesac.backend.orders.domain.OrderedCourses;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDto {

    private Long orderId;

    private String merchantUid;

    private Long userId;

    private Integer totalAmount;

    private String orderStatus;

    private List<OrderedCourses> orderedCourses = new ArrayList<>();
}
