# 새싹 LMS (Learning Management System) - Backend

[![CI/CD](https://github.com/SesacMegazoneLMS/sesac-lms-backend/actions/workflows/workflow.yml/badge.svg?branch=main_LMS)](https://github.com/SesacMegazoneLMS/sesac-lms-backend/actions/workflows/workflow.yml)

## 프로젝트 소개
새싹 LMS는 교육 과정 관리를 위한 학습관리시스템입니다. 본 저장소는 백엔드 서버 코드를 포함하고 있습니다.

## 팀원
- **팀장**: 신동진 (프로젝트 관리 및 사용자 CRUD)
- **팀원**: 성기범 (결제 CRUD)
- **팀원**: 정진욱 (강좌 CRUD)
- **팀원**: 홍인표 (강의 CRUD)

## 기술 스택
- Java
- Spring Boot
- Gradle
- Spring Data JPA
- Spring Security
- PostgreSQL

### 필수 요구사항
- JDK 17 이상
- Gradle 7.x
- PostgreSQL 15.x

## 환경 설정
- application.yml 파일에서 데이터베이스 및 서버 설정을 관리합니다.
- 환경변수를 통해 민감한 정보를 관리합니다.

## CI/CD
GitHub Actions를 통해 자동 빌드 및 배포가 구성되어 있습니다.
