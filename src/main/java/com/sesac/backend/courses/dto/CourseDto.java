package com.sesac.backend.courses.dto;

import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.dto.request.LectureRequest;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseDto {

    private Long id;

    private Long instructorId;

    private String title;

    private String description;

    private String level;

    private String category;

    private BigDecimal price;

    private String thumbnail;

    private List<String> objectives = new ArrayList<>();

    private List<String> requirements = new ArrayList<>();

    private List<String> skills = new ArrayList<>();

    private List<LectureRequest> lectures = new ArrayList<>();
}
