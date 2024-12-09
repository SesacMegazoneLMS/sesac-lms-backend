package com.sesac.backend.quizzes.dto.request;

import com.sesac.backend.quizProblems.dto.request.QuizProblemCreationDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizCreationRequest {

    private Long courseId;

    private Integer quizNumber;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<QuizProblemCreationDto> quizProblems;
}
