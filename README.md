# 곳감 (GotGam) - 여행 다이어리 서비스

## 프로젝트 개요

곳감은 사용자들이 자신의 여행 경험을 기록하고 공유할 수 있는 여행 다이어리 서비스입니다. 사용자는 여행 일정, 비용, 장소 등의 정보와 함께 다이어리를 작성하고, 이를 다른 사용자와 공유할 수 있습니다.

## 주요 기능

- 다이어리 작성 및 관리: 여행 일정, 장소, 비용 등을 포함한 다이어리 작성
- 태그 기능: 다이어리에 태그를 추가하여 분류
- 검색 기능: 키워드, 태그, 지역, 가격 범위 등으로 다이어리 검색
- 회원 관리: 회원 가입, 로그인, 프로필 관리
- 실시간 알림: 댓글, 좋아요 등에 대한 실시간 알림

## 기술 스택

### 백엔드
- Java 17
- Spring Boot 2.7.17
- Spring Data JPA
- Spring Security
- Spring WebSocket
- Elasticsearch
- Redis
- MySQL 8.0
- JWT 인증

### 프론트엔드
- React 19.0.0
- Redux 5.0.1
- TypeScript 4.9.5
- Axios
- styled-components
- React Router
- React Hook Form

### 개발 및 배포 환경
- Gradle
- Docker & Docker Compose
- Jenkins (CI/CD)
- Nginx
- AWS EC2, S3, RDS

## 시스템 아키텍처

본 프로젝트는 헥사고날 아키텍처(Hexagonal Architecture)를 적용하여 도메인 로직과 인프라 로직을 분리하였습니다.

- **API 레이어**: 외부 요청을 처리하는 컨트롤러와 DTO
- **도메인 레이어**: 비즈니스 로직, 도메인 모델, 서비스
- **인프라 레이어**: 데이터베이스 접근, 외부 API 통신
- **코어 레이어**: 공통 유틸리티 및 예외 처리

## 프로젝트 구조
