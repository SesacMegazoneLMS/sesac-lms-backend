package com.sesac.backend.statistics.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sesac.backend.audit.BaseEntity;
import com.sesac.backend.statistics.dto.MonthlyStatsData;
import com.sesac.backend.users.domain.User;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Entity
@Table(name = "instructor_statistics")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
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
    @Type(JsonBinaryType.class)
    private String monthlyStats;

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void updateMonthlyStats(int year, int month, MonthlyStatsData data) {

        log.info("Entering updateMonthlyStats - year: {}, month: {}, data: {}", year, month, data);

        try {
//            ObjectNode rootNode;
//            log.info("Current monthlyStats value: {}", monthlyStats);
//            if (monthlyStats == null || monthlyStats.isEmpty()) {
//                log.info("Creating new root node");
//                rootNode = objectMapper.createObjectNode();
//            } else {
//                log.info("Parsing existing monthlyStats");
//                rootNode = (ObjectNode) objectMapper.readTree(monthlyStats);
//            }
//
//            log.info("Checking year node: {}", String.valueOf(year));
//            if (!rootNode.has(String.valueOf(year))) {
//                rootNode.putObject(String.valueOf(year));
//            }
//
//            log.info("Attempting to update month data");
//            ((ObjectNode) rootNode.get(String.valueOf(year))).putPOJO(String.valueOf(month), data);
//
//            String newStats = objectMapper.writeValueAsString(rootNode);
//            log.info("New monthlyStats value: {}", newStats);
//
//            this.monthlyStats = newStats;
//            log.info("Successfully updated monthlyStats");

            ObjectNode rootNode;
            if (monthlyStats == null || monthlyStats.isBlank()) {
                rootNode = objectMapper.createObjectNode();
            } else {
                rootNode = (ObjectNode) objectMapper.readTree(monthlyStats);
            }

            String yearKey = String.valueOf(year);
            if (!rootNode.has(yearKey)) {
                rootNode.putObject(yearKey);
            }

            ObjectNode yearNode = (ObjectNode) rootNode.get(yearKey);
            yearNode.set(String.valueOf(month), objectMapper.valueToTree(data));

            this.monthlyStats = objectMapper.writeValueAsString(rootNode);
            log.info("Successfully updated monthlyStats");

        } catch (JsonProcessingException e) {
            log.error("Error in updateMonthlyStats", e);
            throw new RuntimeException("월별 통계 업데이트 중 오류 발생", e);
        } catch (Exception e) {
            log.error("Unexpected error in updateMonthlyStats", e);
            throw new RuntimeException("예상치 못한 오류 발생", e);
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
