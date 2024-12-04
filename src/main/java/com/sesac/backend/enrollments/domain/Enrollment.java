package com.sesac.backend.enrollments.domain;

import com.sesac.backend.courses.domain.Course;
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
    private Long id;

    //User entity 참조 필요 -> 수강 User에 대한 정보

    // Course의 User를 가져오면 강사에 대한 정보를 가져올 수 있음
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
