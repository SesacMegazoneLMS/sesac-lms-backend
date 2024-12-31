package com.sesac.backend.courses.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

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

    private String createdAt;
}
