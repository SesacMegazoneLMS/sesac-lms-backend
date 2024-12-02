package com.sesac.backend.sections.domain;

import com.sesac.backend.courses.domain.Courses;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Sections {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sectionsId;
    @ManyToOne
    @JoinColumn(name = "coursesId", nullable = false)
    private Courses courses;
    private String sectionsName;
    private int sectionsNumber;
}
