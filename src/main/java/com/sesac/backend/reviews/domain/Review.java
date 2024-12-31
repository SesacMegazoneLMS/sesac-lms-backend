package com.sesac.backend.reviews.domain;

import com.sesac.backend.audit.BaseEntity;
import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.users.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = {"writer", "course"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private Integer rating;
    private String content;
    private Integer likes = 0;
    private Boolean helpful;
}
