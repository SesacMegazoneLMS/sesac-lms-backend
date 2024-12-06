package com.sesac.backend.quizzes.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizReadResponse {

    private Long id;

    private String title;
}
