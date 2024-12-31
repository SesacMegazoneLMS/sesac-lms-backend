package com.sesac.backend.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatsData {

    private BigDecimal revenue;

    private int newStudents;

    private double completionRate;

    private double averageRating;
}
