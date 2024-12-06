package com.sesac.backend.quizzes.dto.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizCreationResponse {

    private Integer quizNumber;

    private Long courseId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
