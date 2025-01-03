package com.sesac.backend.reviews.dto.response;

import lombok.*;

import java.time.LocalDateTime;

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

    private String createdAt;

}
