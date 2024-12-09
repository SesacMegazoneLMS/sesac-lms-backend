package com.sesac.backend.quizProblems.dto.request;

import com.sesac.backend.quizProblems.enums.Answer;
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
public class QuizProblemAnswerDto {

    private Long problemId;

    private Integer problemNumber;

    private Answer selectedAnswer;
}
