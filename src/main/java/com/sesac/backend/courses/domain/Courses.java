package com.sesac.backend.courses.domain;

import com.sesac.backend.courses.enums.Category;
import com.sesac.backend.courses.enums.Level;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Courses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;
    private UUID instructorId;
    private String title;
    private String description;
    private Level level;
    private Category category;
    private BigDecimal price;
    private String thumbnail;
    private BigDecimal rating;
}
