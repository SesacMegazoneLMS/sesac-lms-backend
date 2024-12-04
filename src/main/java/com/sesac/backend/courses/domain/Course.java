package com.sesac.backend.courses.domain;

import com.sesac.backend.audit.BaseEntity;
import com.sesac.backend.courses.enums.Category;
import com.sesac.backend.courses.enums.Level;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "sections")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false)
//    private User

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
    @Column(nullable = false)
    private List<String> objectives = new ArrayList<>();

    @ElementCollection
    @Column(nullable = false)
    private List<String> requirements = new ArrayList<>();

    @ElementCollection
    @Column(nullable = false)
    private List<String> skills = new ArrayList<>();
}
