package com.sesac.backend.orders.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.orders.constants.OrderStatus;
import com.sesac.backend.orders.domain.Order;
import com.sesac.backend.orders.domain.OrderedCourses;
import com.sesac.backend.orders.dto.request.OrderRequest;
import com.sesac.backend.orders.dto.request.OrderedCourseInfoForRequest;
import com.sesac.backend.orders.dto.response.OrderResponse;
import com.sesac.backend.orders.repository.OrderRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, UUID userId) {

        User user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다"));

        // 주문 생성
        Order order = Order.builder()
                .merchantUid("ORD-" + System.currentTimeMillis())
                .user(user)
                .totalAmount(orderRequest.getTotalAmount())
                .orderStatus(OrderStatus.PENDING)
                .orderedCourses(new ArrayList<>())
                .build();

        System.out.println("order: " + order);
        System.out.println("orderedCourses: " + order.getOrderedCourses());

        // 주문 상품 추가
        for (OrderedCourseInfoForRequest info : orderRequest.getCourses()) {

            Course course = courseRepository.findById(info.getCourseId())
                    .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다"));

            OrderedCourses orderedCourses = OrderedCourses.builder()
                    .course(course)
                    .order(order)
                    .price(info.getPrice())
                    .build();

            orderedCourses.setPrice((int) (orderedCourses.getPrice() * 0.8));

            order.getOrderedCourses().add(orderedCourses);

        }

        System.out.println("orderLast: " + order);
        System.out.println("orderLastCourses: " + order.getOrderedCourses());

        order = orderRepository.save(order);

        System.out.println("isPassed?");

        return OrderResponse.builder()
                .nickname(order.getUser().getNickname())
                .merchantUid(order.getMerchantUid())
                .totalAmount(order.getTotalAmount())
                .build();
    }
}
