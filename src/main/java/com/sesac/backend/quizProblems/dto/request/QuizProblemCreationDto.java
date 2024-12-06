package com.sesac.backend.quizProblems.dto.request;

import com.sesac.backend.quizProblems.enums.Answer;
import com.sesac.backend.quizProblems.enums.Difficulty;
import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizProblemCreationDto {

    private Integer problemNumber;

    private String question;

    private Difficulty difficulty;

    private Answer correctAnswer;

    private List<String> choices;
}
