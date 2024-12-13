package com.sesac.backend.enrollments.dto.response;

import com.sesac.backend.orders.dto.response.OrderedCoursesDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentResponse {

    private Long courseId;
    private String title;
    private String description;
    private String thumbnail;
    private String level;
    private String category;
    private List<String> objectives;
    private Integer price;
    private Integer progress;
    private LocalDateTime enrolledAt;
    private List<EnrolledLectureDto> lectures;

}

