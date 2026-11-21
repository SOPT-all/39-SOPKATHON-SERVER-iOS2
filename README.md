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

## 📡 API

- 📄 API 명세: [Swagger UI](https://api.sopkathon.chokoring.com/swagger-ui/index.html)
- 헬스체크: `GET /actuator/health`

### 공통 응답 형식

```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "성공입니다.",
  "result": { }
}
```

```json
{
  "isSuccess": false,
  "code": "COMMON404",
  "message": "요청한 API를 찾을 수 없습니다.",
  "result": null
}
```

## 📁 프로젝트 구조

```
src/main/java/org/sopt/sopkathon
├── global          # 공통 응답·코드, 예외 처리, 설정, Swagger
└── {domain}        # code, controller, domain, dto, repository, service
```

> 도메인 내부 패키지 구조는 [main 코드 컨벤션](docs/MAIN_CONVENTION.md#1-패키지-구조)을 참고하세요.

## ⚙️ 로컬 실행

```bash
# 1. 환경 변수 설정
cp dev/.env.example dev/.env    # MYSQL_ROOT_PASSWORD 입력

# 2. MySQL 실행 (첫 실행은 초기화에 10~20초 걸리니 healthy 상태가 된 뒤 서버 실행)
docker compose --env-file dev/.env -f dev/docker-compose.dev.yml up -d

# 3. 서버 실행
./gradlew bootRun

# 테스트
./gradlew test
```

> ⚠️ 서버를 실행할 때마다 테이블을 새로 만들고 `data.sql`로 초기 데이터를 넣습니다. 재시작하면 기존 데이터는 초기화됩니다.

## 🚢 운영 배포

`🚀 release` 라벨을 붙인 `develop` → `main` PR이 머지되면 GitHub Actions가 배포합니다.

- 라벨을 붙이면 PR 제목이 다음 버전(첫 릴리즈는 `Release v1.0.0`)으로 자동 변경됩니다.
- 머지되면 해당 버전으로 Git 태그와 GitHub Release가 만들어집니다. 버전은 patch만 1씩 올라갑니다.

```
GitHub Actions ─ 이미지 빌드 → ECR push
               ─ Secrets/Variables → Parameter Store(/sopkathon/prod/*)
               ─ SSM으로 EC2에서 prod/deploy.sh 실행
EC2 ─ Parameter Store를 읽어 .env 생성 → docker compose (Caddy, App, MySQL)
```

> ⚠️ 운영 서버도 배포할 때마다 테이블을 새로 만들고 `data.sql`로 초기 데이터를 넣습니다. 배포하면 기존 데이터는 초기화됩니다.

### 운영 DB 접속

AWS IAM User가 필요합니다. (권한 요청은 인프라 담당에게)  
[Session Manager Plugin](https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html) 설치 후 실행하고, 창을 띄워둔 채로 접속합니다.

```bash
aws ssm start-session --region ap-southeast-2 --target <EC2_INSTANCE_ID> --document-name
AWS-StartPortForwardingSession --parameters "portNumber=3306,localPortNumber=13306"
```

- DataGrip: `localhost:13306`, DB `sopt_sopkathon`

## 📋 컨벤션

### 문서
- 📘 [main 코드 컨벤션](docs/MAIN_CONVENTION.md)
- 🤖 [테스트 코드 작성 지침 (AI용)](docs/AI_TEST_GUIDE.md)

### Branch

```
main ← {타입}/#{이슈번호}-{작업}
```

- 이슈를 먼저 만들고, 이슈 번호로 브랜치를 만듭니다. (`task/#2-init`)
- `develop`으로 PR을 올리고 리뷰 후 머지합니다.

### Commit

```
타입: 작업 내용 (#이슈번호)
```

| 타입 | 설명 |
|---|---|
| `init` | 프로젝트 초기 설정 |
| `feat` | 새로운 기능 |
| `fix` | 버그 수정 |
| `hotfix` | 배포된 서버의 긴급 수정 |
| `refactor` | 동작 변경 없는 코드 개선 |
| `test` | 테스트 추가·수정 |
| `docs` | 문서 수정 |
| `chore` | 빌드, 설정, 의존성 변경 |
| `infra` | 배포, CI/CD, 인프라 설정 |

예) `feat: 공통 응답, 예외 처리 및 Swagger 설정 추가 (#2)`

<!-- CI/CD, 아키텍처 등 추후 추가 예정 -->
