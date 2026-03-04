<h1 align="center">Impact Design Canvas - Backend</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.1-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.0.1"/>
  <img src="https://img.shields.io/badge/MariaDB-003545?logo=mariadb&logoColor=white" alt="MariaDB"/>
  <img src="https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white" alt="Spring Security"/>
  <img src="https://img.shields.io/badge/OpenAI-GPT--4.1-412991?logo=openai&logoColor=white" alt="OpenAI"/>
  <img src="https://img.shields.io/badge/Docs-REST%20Docs-6DB33F" alt="REST Docs"/>
  <img src="https://img.shields.io/badge/License-Private-gray" alt="License"/>
</p>

---

## 📌 소개

**Impact Design Canvas**는 조직의 성과관리를 위한 AI 교육 시스템입니다.

> 🎓 **학생** — 6단계 캔버스 과정을 수행하며 성과 설계 역량을 학습합니다.

> 👨‍🏫 **강사** — 수업 생성, 학생 제출물 조회, 벌크 리포트 다운로드 등 수업 전반을 관리합니다.

### 🧭 학생 워크플로우

```
 ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
 │    A    │───>│    B    │───>│    C    │───>│    D    │───>│    E    │───>│    F    │
 │ Impact  │    │Identity │    │Perform- │    │  Quick  │    │  Build  │    │ Impact  │
 │  Check  │    │ Canvas  │    │ance Flow│    │   Win   │    │   Win   │    │ Review  │
 └─────────┘    └─────────┘    └─────────┘    └─────────┘    └─────────┘    └─────────┘
   자가 진단      비전/미션       실행 흐름       빠른 실행       장기 실행       종합 검토
                   설정            설계            과제            과제
```

---

## 🧰 Tech Stack

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.1 |
| Database | MariaDB + Spring Data JPA |
| Auth | JWT (JJWT 0.11.2) + Spring Security |
| AI | OpenAI API (GPT-4.1) |
| File Storage | Local (`./uploads`) |
| Docs | Spring REST Docs |
| Build | Gradle |

---

## 🏗 Architecture

계층형 아키텍처에 CQRS(Reader/Appender) 패턴을 적용합니다.

```
Controller → Service → Facade/Implementation → Domain Repository → RepositoryImpl → JPA Repository
```

- **Domain Model**: JPA 의존 없는 순수 POJO (`XxxModel`)
- **Storage Layer**: Entity ↔ Model 변환은 `RepositoryImpl.toModel()`에서 수행
- **Response**: `ResponseHelper.success(dto)` 래핑, DTO는 `@Builder` + `from()` 팩토리 메서드

---

## 📁 Package Structure

```
qtedu.Impact_design/
├── api/
│   ├── controller/          # REST 컨트롤러 (14개)
│   ├── config/              # Security, Web, SPA, AI, S3 설정
│   ├── dto/request/         # 요청 DTO (기능별)
│   ├── dto/response/        # 응답 DTO (기능별)
│   └── util/security/       # JWT 필터, @CurrentUser
├── domain/
│   ├── service/             # 서비스 (위임 역할)
│   ├── implementation/      # 비즈니스 로직 (Reader/Appender/Updater)
│   ├── repository/          # 도메인 레포지토리 인터페이스
│   ├── model/               # 도메인 모델 (순수 POJO)
│   └── external/            # 외부 서비스 인터페이스
├── storage/
│   ├── jpaentity/           # JPA 엔티티
│   ├── jparepository/       # Spring Data JPA
│   └── repository/          # 레포지토리 구현체
├── external/                # 외부 서비스 구현 (OpenAI, S3, LocalFile)
└── common/
    ├── error/               # 예외 클래스 (NotFoundException, ConflictException 등)
    └── response/            # 공통 응답 (HttpResponse, ErrorResponse)
```

---

## ✨ Features

### 📋 6단계 캔버스

| 단계 | 모듈 | 설명 |
|------|------|------|
| **A** | Impact Check | 자가 진단 (현재 성과 수준 점검) |
| **B** | Identity Canvas | 비전/미션 설정 (가치, 조직 맥락 분석) |
| **C** | Performance Flow | 실행 흐름 설계 (전략활동, 전술, 성과, 임팩트) |
| **D** | Quick Win | 빠른 실행 과제 (단기 3~6개월) |
| **E** | Build Win | 장기 실행 과제 (1~2년) |
| **F** | Impact Review | 종합 검토 (팀 간 상호 투자 평가) |

### 🔧 지원 기능

- 🔐 **Auth**: 학생 회원가입, 강사/관리자 로그인, 팀 배정
- 📚 **Teach**: 수업 생성/관리, 팀 구성, 학생 진행도 추적
- 📊 **Report**: 팀 성과 리포트, AI 기반 분석, 벌크 다운로드
- 📎 **Media**: 파일 업로드 (캔버스 첨부파일)

---

## 🌐 API Endpoints

| Path | 설명 |
|------|------|
| `POST /api/auth/**` | 인증 (로그인, 회원가입, 코드 확인) |
| `GET /api/game/step` | 현재 진행 단계 조회 |
| `*/api/identity-canvas/**` | B단계 - Identity Canvas CRUD |
| `*/api/flow-canvas/**` | C단계 - Performance Flow CRUD |
| `*/api/quick-win-canvas/**` | D단계 - Quick Win CRUD |
| `*/api/build-win-canvas/**` | E단계 - Build Win CRUD |
| `*/api/impact-check/**` | A단계 - Impact Check CRUD |
| `*/api/funding/**` | F단계 - Impact Review |
| `*/api/teach/**` | 강사 수업/팀/제출물 관리 |
| `*/api/teach/report/**` | 팀 성과 리포트 |
| `*/api/admin/**` | 관리자 대시보드 |
| `POST /api/ai/chat` | AI 채팅(테스트용) |

### 📄 API 문서 (Spring REST Docs)

배포 후 `/docs/index.html`에서 확인할 수 있습니다.

```
빌드 시 자동 생성:
  테스트 실행 → Asciidoctor 스니펫 생성 → HTML 변환 → static/docs/index.html
```

| 테스트 클래스 | 커버 영역 |
|--------------|----------|
| `AuthControllerTest` | 로그인, 회원가입, 코드 확인, 로그아웃 |
| `ImpactCheckControllerTest` | A단계 - 자가 진단 |
| `IdentityCanvasControllerTest` | B단계 - 비전/미션 설계 |
| `FlowCanvasControllerTest` | C단계 - 성과경로 설계 |
| `QuickWinCanvasControllerTest` | D단계 - Quick Win |
| `BuildWinCanvasControllerTest` | E단계 - Build Win |
| `FundingControllerTest` | F단계 - 투자 평가 |
| `GameControllerTest` | 학생 대시보드 |
| `TeachControllerTest` | 강의실 관리 |
| `TeachTeamControllerTest` | 팀 관리 |
| `TeachSubmissionControllerTest` | 제출물 조회 |
| `ReportControllerTest` | 리포트 생성 |
| `AdminControllerTest` | 관리자 기능 |

---

## 👥 User Roles

| 역할 | 설명 |
|------|------|
| 🎓 **STUDENT** | 팀에 소속되어 6단계 캔버스 작성/제출 |
| 👨‍🏫 **TEACHER** | 수업 생성, 팀 관리, 제출물 확인, 벌크 리포트 다운로드 |
| 🔑 **ADMIN** | 전체 수업 조회/관리 |

---

## 🗄 Database Schema

### 테이블 관계도 (논리적, FK 제약조건 없음 — 애플리케이션 레벨 조인)

```
 userinfo ─────┬──── identity_canvas      (B단계, user_id로 연결)
   │           ├──── Impact_check          (A단계, user_id로 연결)
   │           ├──── flow_canvas           (C단계, user_id로 연결)
   │           │       ├── strategic_activity  (goal_id로 연결)
   │           │       └── tactical            (goal_id로 연결)
   │           ├──── win_canvas            (D/E단계, user_id로 연결)
   │           │       ├── task_activity       (canvas_id로 연결)
   │           │       ├── task_input          (canvas_id로 연결)
   │           │       ├── task_outcome        (canvas_id로 연결)
   │           │       └── teamwork            (canvas_id로 연결)
   │           └──── logged_in             (세션, userNo로 연결)
   │
   └── teamuser ──── tbteam ──── tbgame
          (user_id,     (game_id,      ├── tbmission ── tbmissiondata
           team_id)      team_id)      ├── f_letter_of_intent   (F단계)
                                       ├── f_letter_of_intent2  (F단계)
                                       └── contents
```

> DB에 FK 제약조건이 없으며, 모든 관계는 애플리케이션 코드에서 ID 값으로 조회합니다.

### 👤 사용자/인증

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `userinfo` | 사용자 | `user_id` (PK), `id` (로그인ID), `pwd`, `user_name`, `code`, `user_role` (STUDENT/TEACHER/ADMIN) |
| `logged_in` | 로그인 세션 | `logged_in_id` (PK), `refresh_token`, `expired_at`, `userNo` |
| `tbrole` | 역할 정의 | `role_id` (PK), `role_code`, `name`, `description` |
| `userrole` | 역할 할당 | `user_role_id` (PK), `role_id`, `isDoing`, `powerlevel` |

### 📚 수업/팀 관리

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `tbgame` | 수업 | `game_id` (PK), `name`, `code`, `status`, `step`, `is_doing`, `num_team`, `image_url`, `target`, `project_date` |
| `tbteam` | 팀 | `team_id` (PK), `name`, `sequence`, `code`, `num_user` |
| `teamuser` | 팀-사용자 매핑 | `team_user_id` (PK), `user_id`, `team_id`, `userlevel` |
| `gameteam` | 수업-팀 매핑 | `game_id` + `team_id` (복합PK) |
| `gameadmin` | 수업-강사 매핑 | `game_id` + `user_id` (복합PK) |
| `tbmission` | 미션 | `mission_id` (PK), `sequence`, `subject`, `summary`, `game_id` |
| `tbmissiondata` | 미션 데이터 | `mission_data_id` (PK), `team_id`, `mission_id`, 부서별 status |
| `contents` | 콘텐츠 | `contents_id` (PK), `team_id`, `game_id`, `subject`, `detail`, 파일 정보 |

### 📊 A단계 - Impact Check

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `Impact_check` | 자가 진단 | `answer_id` (PK), `q1_score`~`q12_score` (점수), `q13_text`~`q16_text` (주관식), `user_id` (UNIQUE), `submitted` |

### 🧩 B단계 - Identity Canvas

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `identity_canvas` | 비전/미션 | `identity_id` (PK), `mission`, `vision`, `value`, `macro`, `tech`, `customer`, `competitor`, `capability`, `culture`, `structure`, `new_mission`, `new_vision`, `new_value`, `user_id` (UNIQUE), `submitted` |

### 📈 C단계 - Performance Flow

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `flow_canvas` | 성과 흐름 | `goal_id` (PK), `goal_title`, `goal_description`, `order_no`, `user_id`, `submitted` |
| `strategic_activity` | 전략 활동 | `activity_id` (PK), `activity_metric`, `inter_criteria`, `order_no`, `goal_id` |
| `tactical` | 전술 | `metric_id` (PK), `tactical_metric`, `tactical_goal`, `order_no`, `goal_id` |

### ⚡ D/E단계 - Quick Win & Build Win

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `win_canvas` | Win 캔버스 | `canvas_id` (PK), `canvas_type` (QUICK/BUILD), `strategic_goal`, `task_name`, `task_description`, `crisis_signal`, `pain_touch_point`, `user_id`, `submitted` |
| `task_activity` | 실행 활동 | `activity_id` (PK), `process_step`, `activity_content`, `duration`, `order_no`, `canvas_id` |
| `task_input` | 투입 자원 | `input_id` (PK), `resource_name`, `quantity`, `order_no`, `canvas_id` |
| `task_outcome` | 기대 성과 | `outcomeNo` (PK), `outcome_type` (Enum), `outcome_content`, `order_no`, `canvas_id` |
| `teamwork` | 팀워크 | `teamwork_id` (PK), `activity_teamwork`, `work_type`, `canvas_id` |

### 🧪 F단계 - Impact Review (펀딩 시뮬레이션)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `f_letter_of_intent` | 투자의향서 (Build Win) | `intent_index` (PK), `stdntNo`, `investment_target`, `investment_price`, `score1`~`score10`, `opinion`, `team_id`, `game_id`, `canvas_id`, `submitted` |
| `f_letter_of_intent2` | 투자의향서 (Quick Win) | 위와 동일 구조 |

### 📦 기타

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `tbapproval` | 승인 | `approval_id` (PK), `status`, `dd_year`, `dd_term`, `level`, `role`, `game_id`, `team_id` |
| `tbstateapproval` | 상태 승인 | `state_approval_id` (PK), 위와 동일 구조 |
| `bc_missiongame` | 미션 게임 | `bc_mission_game_id` (PK), 부서별 미션 (ceo/cmo/coo/cho/cfo), `game_id` |

---

## 🚀 Getting Started

### 📋 Prerequisites

- Java 17+
- MariaDB
- (선택) AWS S3 버킷

### ⚙ Configuration

`src/main/resources/application-local.yaml` 기준:

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/impact_design
    username: root
    password: your_password

jwt:
  secret: your_jwt_secret_key

openai:
  api-key: your_openai_api_key
```

### 🔨 Build & Run

```bash
# 빌드 (테스트 포함)
./gradlew clean bootJar

# 빌드 (테스트 스킵)
./gradlew clean bootJar -x test -x asciidoctor

# 실행
java -jar build/libs/Impact_design-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

### 📂 Profiles

| Profile | 용도 |
|---------|------|
| `local` | 로컬 개발 (localhost DB, CORS: localhost:3000) |
| `prod` | 운영 (환경변수 기반 DB, 실서버 CORS) |
| `test` | 테스트 (DataSource/JPA 비활성화) |

---

## 🚢 배포 구조

`main` 브랜치 push 시 GitHub Actions가 자동 배포합니다. (`.github/workflows/deploy.yml`)

```
 [개발자 PC]
      │
      │ git push (main)
      ▼
  [GitHub Actions]  ← 빌드는 여기서 (서버 리소스 안 씀)
      │ 1. Gradle로 JAR 빌드
      │ 2. SCP로 JAR 전송
      │ 3. SSH로 재시작 명령
      ▼
  [서버 - Rocky Linux 1GB RAM]
      │
      ├─ systemd (impact.service)
      │   └─ JVM + Spring Boot (내장 Tomcat, :8080)
      │
      └─ MariaDB (:3306)
```

### 🔑 환경변수

운영 서버에서 `/root/.env`에 아래 환경변수를 설정해야 합니다.

| 변수 | 설명 | 필수 |
|------|------|------|
| `JWT_SECRET` | JWT 서명 키 | ✅ |
| `OPENAI_API_KEY` | OpenAI API 키 | ✅ |
| `DB_USERNAME` | MariaDB 사용자명 | ✅ |
| `DB_PASSWORD` | MariaDB 비밀번호 | ✅ |

> `impact.service`에 `EnvironmentFile=/root/.env`가 설정되어 있어야 합니다.

### 🖥 서버 관리 명령어

```bash
systemctl status impact               # 상태 확인
sudo systemctl restart impact          # 재시작
sudo systemctl stop impact             # 중지
journalctl -u impact -n 50 --no-pager  # 최근 로그 50줄
journalctl -u impact -f                # 실시간 로그
```

---

## 🪪 License

Private
