package com.sesac.backend.quizzes.dto.request;

import com.sesac.backend.quizProblems.dto.request.QuizProblemAnswerDto;
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
public class QuizSubmissionRequest {

    private Long quizId;

    private List<QuizProblemAnswerDto> answers;
}
