package com.sesac.backend.reviews.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewRequest {
    private Long id;

    // course에 대한 정보 조회를 위한 course pk
    private Long courseId;

    // 수강평
    private String content;

    // 평점
    private Integer rating;
}
