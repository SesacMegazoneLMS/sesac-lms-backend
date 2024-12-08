package com.sesac.backend.quizzes.dto.response;

import com.sesac.backend.quizProblems.dto.response.QuizProblemDetailDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizDetailResponse {

    private Long quizId;

    private String title;

    private String duration;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer totalQuestions;

    private List<QuizProblemDetailDto> problems;
}
