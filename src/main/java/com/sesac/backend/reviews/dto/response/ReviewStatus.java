package com.sesac.backend.reviews.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewStatus {
    private int reviewCount;

    private double averageRating;
}
