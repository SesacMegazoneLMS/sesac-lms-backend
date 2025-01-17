package com.sesac.backend.courses.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sesac.backend.audit.BaseEntity;
import com.sesac.backend.courses.enums.Category;
import com.sesac.backend.courses.enums.Level;
import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.users.domain.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Getter
@Setter
@ToString(exclude = {"lectures", "reviews"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"instructor_id", "title"}))
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    private String thumbnail;

    @ElementCollection
    private List<String> objectives = new ArrayList<>();

    @ElementCollection
    private List<String> requirements = new ArrayList<>();

    @ElementCollection
    private List<String> skills = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>(); // Course에 대한 리뷰


}
