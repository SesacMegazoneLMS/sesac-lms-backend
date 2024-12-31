package com.sesac.backend.lectures.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatsResponse {
    private int totalHours;      // 총 학습 시간(시간)
    private int weeklyHours;     // 주간 학습 시간(시간)
    private long completedCourses; // 완료한 강의 수
}
