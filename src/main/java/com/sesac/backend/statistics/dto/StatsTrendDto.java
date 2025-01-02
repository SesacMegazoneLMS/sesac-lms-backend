package com.sesac.backend.statistics.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsTrendDto {

    private int value;
    private double trend;  // 증감률
    private String trendType;  // INCREASE, DECREASE, NO_CHANGE
    private boolean isNew;

}
