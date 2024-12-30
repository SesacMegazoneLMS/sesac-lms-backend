package com.sesac.backend.lectures.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatsResponse {
    private int totalHours; // 총 학습 시간 (시간 단위)
    private int weeklyStudyHours; // 주간 학습 시간
    private long completedLectures; // 완료한 강의 수
}
