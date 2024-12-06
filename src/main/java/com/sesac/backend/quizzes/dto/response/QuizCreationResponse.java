package com.sesac.backend.quizzes.dto.response;

import java.time.LocalDateTime;
import lombok.*;

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
