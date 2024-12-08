package com.sesac.backend.quizProblems.dto.response;

import com.sesac.backend.quizProblems.enums.Answer;
import com.sesac.backend.quizProblems.enums.Correctness;
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
public class QuizProblemResultDto {

    private Long problemId;

    private Integer number;

    private Correctness correctness;

    private Integer difficulty;

    private Answer correctAnswer;

    private Answer selectedAnswer;

    private String question;

    private List<String> choices;
}
