package com.sesac.backend.statistics.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sesac.backend.audit.BaseEntity;
import com.sesac.backend.statistics.dto.MonthlyStatsData;
import com.sesac.backend.users.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "instructor_statistics")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstructorStats extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer totalStudents;

    private Integer activeCourses;

    private BigDecimal totalRevenue;

    private Double averageRating;

    @Column(columnDefinition = "jsonb")
    private String monthlyStats;

    @Transient
    private ObjectMapper objectMapper = new ObjectMapper();

    public void updateMonthlyStats(int year, int month, MonthlyStatsData data) {

        try {
            ObjectNode rootNode;
            if (monthlyStats == null || monthlyStats.isEmpty()) {
                rootNode = objectMapper.createObjectNode();
            } else {
                rootNode = (ObjectNode) objectMapper.readTree(monthlyStats);
            }
            if (!rootNode.has(String.valueOf(year))) {
                rootNode.putObject(String.valueOf(year));
            }

            ((ObjectNode) rootNode.get(String.valueOf(year))).putPOJO(String.valueOf(month), data);

            this.monthlyStats = objectMapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("월별 통계 업데이트 중 오류 발생", e);
        }

    }

    public MonthlyStatsData getMonthlyStats(int year, int month) {
        try {
            if (monthlyStats == null || monthlyStats.isEmpty()) {
                return null;
            }
            JsonNode rootNode = objectMapper.readTree(monthlyStats);
            JsonNode yearNode = rootNode.get(String.valueOf(year));
            if (yearNode == null) {
                return null;
            }
            JsonNode monthNode = yearNode.get(String.valueOf(month));
            if (monthNode == null) {
                return null;
            }

            return objectMapper.treeToValue(monthNode, MonthlyStatsData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("월별 통계 조회 중 오류 발생", e);
        }
    }
}
