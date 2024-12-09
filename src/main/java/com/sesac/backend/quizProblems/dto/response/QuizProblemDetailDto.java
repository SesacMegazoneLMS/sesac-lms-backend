package com.sesac.backend.quizProblems.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizProblemDetailDto {

    private Long problemId;

    private Integer number;

    private String question;

    private List<String> options;
}
