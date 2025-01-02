package com.sesac.backend.statistics.dto;

import com.sesac.backend.courses.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CourseIdsDto {

    private List<Course> courses;
    private List<Long> courseIds;
    private List<Long> sortedCourseIds;
    private List<Long> distinctCourseIds;
}
