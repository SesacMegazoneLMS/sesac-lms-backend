package com.sesac.backend.enrollments.domain;

import com.sesac.backend.orders.domain.OrderedCourses;
import com.sesac.backend.users.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderedCoursesId")
    private OrderedCourses orderedCourses;

    private boolean isActive;
}
