package com.sesac.backend.statistics.dto;

import com.sesac.backend.users.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstructorStatsDto {

    private Long id;

    private Long instructorId;

    private int totalStudents;

    private int activeCourses;

    private BigDecimal monthlyRevenue;

    private BigDecimal totalRevenue;

    private Double averageRating;
}
