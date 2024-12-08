package com.sesac.backend.quizzes.dto.response;

import com.sesac.backend.quizProblems.dto.response.QuizProblemResultDto;
import java.time.LocalDateTime;
import java.util.List;
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
public class QuizResultResponse {

    private Long quizId;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer score;

    private List<QuizProblemResultDto> problemResults;
}
