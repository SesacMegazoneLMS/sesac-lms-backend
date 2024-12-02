package com.sesac.backend.courses.domain;

import com.sesac.backend.courses.enums.Category;
import com.sesac.backend.courses.enums.Level;
import com.sesac.backend.sections.domain.Section;
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
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();
    private UUID instructorId;
    private String title;
    private String description;
    private Level level;
    private Category category;
    private BigDecimal price;
    private String thumbnail;
    private BigDecimal rating;
}
