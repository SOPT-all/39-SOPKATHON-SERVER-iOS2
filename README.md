# 39-SOPKATHON-SERVER-iOS2

> PLAY SOPT 39기 솝커톤 iOS 2조

## 👥 서버 팀

| [조효림](https://github.com/hyorim-jo) | [김예지](https://github.com/apffkxhsls) | [김민교](https://github.com/mindolii) |
| :---: | :---: | :---: |
| <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQLDCVlQg1BsKKu8n9M_Tc3Yf0m9g_FSr5yWEJnKjELvA&s=10" width="120" alt="조효림" /> | <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQEEu3hosEGEGDg0J8hJ8fTsD3WAUGglkz1rX9_z45_kw&s=10" width="120" alt="김예지" /> | <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSrjRCa4iTUsyHC40lt4wLdanTRUWMdYZHTjigKiFqDIg&s=10" width="120" alt="김민교" /> |
| `Server Lead` | `Server Developer` | `Server Developer` |

## 🛠️ 기술 스택

<p>
  <img alt="Java 21" src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img alt="Swagger" src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" />
  <br />
  <img alt="Docker" src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
  <img alt="GitHub Actions" src="https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white" />
  <img alt="Terraform" src="https://img.shields.io/badge/Terraform-844FBA?style=for-the-badge&logo=terraform&logoColor=white" />
  <img alt="AWS" src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge" />
</p>

- **서버** - Spring Web MVC, Bean Validation, Lombok
- **DB** - Spring Data JPA, QueryDSL (OpenFeign 7.x), MySQL Connector/J
- **API 문서 / 헬스체크** - springdoc-openapi 3.x, Spring Boot Actuator
- **테스트** - Spring Boot Web MVC Test, Spring Boot Data JPA Test, H2, ArchUnit
- **포맷 / 커버리지** - Spotless, JaCoCo
- **배포** - Docker Compose, EC2, ECR, S3, Caddy

## 🚀 개발 환경

| 항목 | 초기 구성 |
| --- | --- |
| 실행 환경 | JDK 21, Docker |
| Spring 프로필 | `dev`, `prod` |
| 로컬 DB | Docker Compose로 MySQL 실행 |
| API 공통 설정 | 공통 응답 포맷, 전역 예외 처리 |
| 시간 설정 | KST 기준 `Clock`, JPA Auditing |
| API 및 상태 확인 | Swagger UI, Actuator 헬스체크 |

## 📋 컨벤션

<!-- 컨벤션 문서 링크를 추가합니다. -->

<!-- CI/CD, 아키텍처 등 추후 추가 예정 -->
