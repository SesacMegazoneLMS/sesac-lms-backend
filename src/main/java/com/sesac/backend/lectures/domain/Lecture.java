package com.sesac.backend.lectures.domain;

import com.sesac.backend.sections.domain.Section;
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
    private String title;
    private String duration;
    private Boolean isFree;
    private String videoUrl;
    private Integer orderIndex;  // 강의 순서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;
}
