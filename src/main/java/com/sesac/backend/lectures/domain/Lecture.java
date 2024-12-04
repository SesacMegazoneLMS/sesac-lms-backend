package com.sesac.backend.lectures.domain;

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
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String title;
    private String duration;
    private String videoUrl;
    private Integer orderIndex;  // 강의 순서
}
