package com.sesac.backend.reviews.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {

    private Long id;

    private String writer;

    private String content;

    private Integer rating;

    private Integer likes;

    private Boolean helpful;

    // 강의 명 및 리뷰 작성일 추가
    private String courseName;

    private String createdAt;

}
