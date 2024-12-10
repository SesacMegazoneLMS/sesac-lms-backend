package com.sesac.backend.orders.domain;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.orders.constants.OrderedCoursesStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = {"order"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderedCourses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer price; // 구매 당시 가격

    @Enumerated(EnumType.STRING)
    private OrderedCoursesStatus status = OrderedCoursesStatus.ACTIVE;

}
