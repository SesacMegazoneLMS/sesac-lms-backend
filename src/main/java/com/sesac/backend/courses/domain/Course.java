package com.sesac.backend.courses.domain;

import com.sesac.backend.courses.enums.Category;
import com.sesac.backend.courses.enums.Level;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "sections")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID instructorId;  // Lambda API를 통해 조회할 User ID

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Category category;

    private BigDecimal price;
    private String thumbnail;
    private BigDecimal rating;
    private Integer students;
    private Integer totalLectures;
    private String totalHours;
    private String lastUpdated;



    @ElementCollection
    private List<String> objectives = new ArrayList<>();

    @ElementCollection
    private List<String> requirements = new ArrayList<>();

    @ElementCollection
    private List<String> skills = new ArrayList<>();
}
