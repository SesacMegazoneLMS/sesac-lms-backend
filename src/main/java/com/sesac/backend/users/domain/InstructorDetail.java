package com.sesac.backend.users.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class InstructorDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
