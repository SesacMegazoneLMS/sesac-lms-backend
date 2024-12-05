package com.sesac.backend.courses.domain;

import com.sesac.backend.audit.BaseEntity;
import com.sesac.backend.courses.enums.Category;
import com.sesac.backend.courses.enums.Level;
import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.users.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"id", "title"}))
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private User user;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Category category;

    private BigDecimal price;
    private String thumbnail;

    @ElementCollection
    private List<String> objectives = new ArrayList<>();

    @ElementCollection
    private List<String> requirements = new ArrayList<>();

    @ElementCollection
    private List<String> skills = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();
}
