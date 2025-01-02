package com.sesac.backend.enrollments.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentEnrollmentDto {

    private Long userId;
    private String username;
    private String courseName;
    private String enrolledAt;

    // JPQL 매핑을 위한 생성자 추가
    public RecentEnrollmentDto(Long userId, String username, String courseName, LocalDateTime enrolledAt) {
        this.userId = userId;
        this.username = username;
        this.courseName = courseName;
        this.enrolledAt = enrolledAt.toString();
    }

}
