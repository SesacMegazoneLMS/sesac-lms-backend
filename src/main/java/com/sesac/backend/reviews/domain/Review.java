package com.sesac.backend.reviews.domain;

import com.sesac.backend.courses.domain.Course;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private Integer rating;
    private String content;
    private Integer likes;
    private Boolean helpful;
}
