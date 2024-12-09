package com.sesac.backend.users.domain;

import com.sesac.backend.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private UUID userId;
    @Column(unique = true)
    private String nickname; // 닉네임(이름)

    private String introduction; // 자기소개
    // 전문 분야
    private String techStack; // 기술 스택, 스트링 직렬화 필요

    // 소셜 링크
    private String websiteUrl; // 개인 블로그
    private String linkedinUrl; // 링크드인
    private String githubUrl; // 깃허브

    // 사용자 프로필 URL
    private String profileImgUrl; // cdn에 등록된 프로필 이미지의 경로
}