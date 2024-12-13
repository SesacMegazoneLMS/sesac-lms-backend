package com.sesac.backend.courses.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseInstructorDto {
    private Long id;

    private String title;

    private int enrollmentCount;

    private double averageRating;
}
