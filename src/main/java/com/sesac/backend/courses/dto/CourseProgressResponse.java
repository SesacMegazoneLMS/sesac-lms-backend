package com.sesac.backend.courses.dto;

// 25.01.03 홍인표 작성. 수강 중인 강좌의 진행률을 반환하는 DTO


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressResponse {
    private Long courseId;
    private int totalLectures;
    private int completedLectures;
    private double progressRate;
}