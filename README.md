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
   자가 진단      비전/미션       실행 흐름       빠른 실행       장기 실행         종합 검토
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
Controller → Service → Implementation(Reader/Appender/Updater/Remover) → Repository(인터페이스) ◄── RepositoryImpl → JPA Repository
   API 경계      트랜잭션 경계        도메인 로직 (CQRS 분리)              도메인 추상화          영속성 어댑터
```

| 레이어 | 책임 | 비고 |
|--------|------|------|
| **Controller** | HTTP 입출력, 인증, DTO 매핑 | `@CurrentUser UserId`로 JWT 추출 |
| **Service** | 트랜잭션 경계, 유스케이스 오케스트레이션 | `@Transactional`이 여기에 붙음 |
| **Implementation** | Reader/Appender/Updater/Remover/Validator로 CQRS 분리 | 변경 이유가 하나뿐인 클래스 유지 |
| **Domain Repository** | 도메인 모듈이 의존하는 순수 인터페이스 | storage 의존 0 |
| **RepositoryImpl** | Entity ↔ Model 변환, JPA 위임 | `toModel()` private 메서드 |

- **Domain Model**: JPA 의존 없는 순수 POJO (`XxxModel`). 도메인/API 레이어는 `storage.jpaentity.*`를 절대 import하지 않습니다.
- **CQRS 분리**: 같은 도메인이라도 읽기(`Reader`)와 쓰기(`Appender`/`Updater`/`Remover`)를 다른 클래스로 분리해, 변경 이유와 트랜잭션 특성을 격리합니다.
- **Response**: `ResponseHelper.success(dto)` 래핑, DTO는 `@Builder` + `from()` 팩토리 메서드.

> **"Service와 Implementation이 둘 다 있는 이유?"** — Service는 트랜잭션 경계와 유스케이스 조립을 담당하고, Implementation은 CQRS 단위로 쪼개진 도메인 로직을 담당합니다. 4단 추상화가 아니라, **하나의 도메인 레이어를 책임 단위(읽기/쓰기/검증)로 수평 분할**한 구조입니다.

---

## 🧱 SOLID 원칙 적용

### S — 단일 책임 원칙 (SRP)

`domain/implementation/` 패키지에서 **Reader/Appender/Updater/Remover/Validator**로 역할을 분리합니다.

| 클래스 | 책임 |
|--------|------|
| `AuthReader` | 인증 데이터 **조회**만 담당 |
| `AuthAppender` | 회원가입, 로그인 토큰 **생성**만 담당 |
| `TeachUpdater` | 강의실 상태 **수정**만 담당 |
| `TeachTeamRemover` | 팀/팀원 **삭제**만 담당 |
| `FileValidator` | 파일명 **검증**만 담당 |

하나의 도메인이라도 읽기/쓰기/삭제/검증을 각각 별도 클래스로 분리하여, 변경 이유가 하나뿐인 클래스를 유지합니다.

### O — 개방-폐쇄 원칙 (OCP)

인터페이스 기반으로 기존 코드 수정 없이 새 구현체를 추가할 수 있습니다.

```java
// domain/repository/ai/AiClient.java
public interface AiClient {
    AiResponse chat(AiRequest request);
    String getProvider();
}
```

새 AI 제공자(Claude, Gemini 등)를 추가할 때 `AiClient` 구현체만 추가하면 되고, `AiClientProvider`나 `AiSender`는 수정할 필요가 없습니다. `ExternalFileClient`도 동일한 방식으로 스토리지 교체를 지원합니다.

### L — 리스코프 치환 원칙 (LSP)

모든 `RepositoryImpl`이 도메인 `Repository` 인터페이스의 계약을 정확히 준수합니다.

```java
// domain/repository/auth/LoggedInRepository.java (인터페이스)
public interface LoggedInRepository {
    Optional<LoggedInModel> findByUserId(Long userId);
    LoggedInModel save(LoggedInModel loggedInModel);
    void deleteByUserId(Long userId);
}

// storage/repository/auth/LoggedInRepositoryImpl.java (구현체)
@Repository
public class LoggedInRepositoryImpl implements LoggedInRepository { ... }
```

`AuthAppender`가 `LoggedInRepository` 타입으로 주입받으므로, 구현체를 다른 것으로 치환해도 동작이 깨지지 않습니다.

### I — 인터페이스 분리 원칙 (ISP)

Repository 인터페이스가 도메인별로 작고 집중적으로 분리되어 있습니다.

| 인터페이스 | 메서드 수 | 관심사 |
|-----------|----------|--------|
| `LoggedInRepository` | 3개 | 세션 관리 |
| `TeamUserRepository` | 5개 | 팀 멤버십 |
| `TbTeamRepository` | 5개 | 팀 데이터 |
| `UserinfoRepository` | 5개 | 사용자 정보 |
| `FlowCanvasRepository` | 5개 | 성과경로 캔버스 |

하나의 거대한 인터페이스 대신 각 도메인에 필요한 메서드만 정의하여, 클라이언트가 사용하지 않는 메서드에 의존하지 않습니다.

### D — 의존성 역전 원칙 (DIP)

이 프로젝트의 핵심 아키텍처 원칙입니다. domain 레이어는 추상화(인터페이스)에만 의존하고, storage 레이어가 이를 구현합니다.

```
[domain 레이어]                          [storage 레이어]
AuthAppender                            LoggedInRepositoryImpl
  └─ LoggedInRepository (인터페이스) ◄──── └─ implements
```

```java
// domain 레이어 — 추상화에만 의존
@Component
public class AuthAppender {
    private final LoggedInRepository loggedInRepository;  // 인터페이스
}

// storage 레이어 — 구현체가 추상화를 구현
@Repository
public class LoggedInRepositoryImpl implements LoggedInRepository {
    private final LoggedInJpaRepository loggedInJpaRepository;  // JPA 상세
}
```

domain 레이어는 `storage/jpaentity`를 절대 import하지 않으므로, JPA → MyBatis 또는 SQL → NoSQL로 변경해도 domain 코드 수정이 필요 없습니다.

---

## 📁 Package Structure

```
qtedu.Impact_design/
├── api/
│   ├── controller/          # REST 컨트롤러 (12개)
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
    ├── error/               # 예외 클래스 (BadRequestException, NotFoundException, ConflictException 등)
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

## 🤖 AI Integration — 운영 관점의 설계

단순 GPT 호출이 아니라, **1GB RAM 서버에서 동시 리포트 생성을 안정적으로 처리하기 위한 동시성/타임아웃/실패 처리** 설계가 핵심입니다.

### 1. 모델 분기

```java
public enum AiModel { GPT_4_1, GPT_4_1_MINI }
AiResponse chat(AiModel model, String systemPrompt, String userPrompt);
```

- 비용/지연이 큰 종합 리포트는 `GPT_4_1`, 단순 분석/요약은 `GPT_4_1_MINI`로 분기.
- `OpenAiClient`는 `AiClient` 인터페이스를 구현 — Claude/Gemini로 교체 시 어댑터만 추가하면 되도록 OCP 준수.

### 2. 백프레셔 (`ioExecutor`)

리포트 생성은 외부 I/O가 길어 무제한 동시 호출 시 서버가 죽습니다. 별도 풀로 격리하고 큐가 가득 차면 호출 스레드가 직접 실행하도록 설정.

| 항목 | 값 | 의도 |
|------|----|------|
| corePoolSize | **5** | 동시 OpenAI 호출 상한 |
| queueCapacity | **50** | 버스트 흡수 |
| RejectedExecutionHandler | **CallerRunsPolicy** | 큐가 가득 차면 호출 스레드가 직접 실행 → 자연스러운 백프레셔, 요청 유실 0 |

> 관련 커밋: `a4209e9 refactor: ioExecutor 백프레셔 도입 (풀 5 / 큐 50 / CallerRunsPolicy)`

### 3. 타임아웃 cascade

OpenAI 호출이 매달리면 상위 HTTP 요청도 같이 매달려 톰캣 워커가 고갈됩니다. **상위 요청의 잔여 데드라인을 OpenAI HTTP 클라이언트까지 전파**해, 상위가 죽기 전에 하위가 먼저 끊기도록 했습니다.

> 관련 커밋: `85e4508 fix: 리포트 생성 시 OpenAI 호출 타임아웃 cascade 적용`

### 4. 응답 파싱과 실패 처리

- OpenAI에 **JSON 응답을 강제**한 뒤 `ObjectMapper`/`JsonNode`로 파싱.
- 파싱 실패/스키마 불일치 시 `BadRequestException`이 아니라 **재시도 가능한 형태로 분리**해, 사용자 입력 오류와 모델 출력 오류를 구분.
- 외부 호출은 **트랜잭션 경계 밖으로 빼서**, AI 실패가 DB 롤백을 유발하지 않게 했습니다 (보상 트랜잭션 모델).

---

## 🌐 API Endpoints

| Path | 설명 |
|------|------|
| `POST /api/auth/**` | 인증 (로그인, 회원가입, 코드 확인) |
| `GET /api/game/step` | 현재 진행 단계 조회 |
| `*/api/impact-check/**` | A단계 - Impact Check CRUD |
| `*/api/identity-canvas/**` | B단계 - Identity Canvas CRUD |
| `*/api/flow-canvas/**` | C단계 - Performance Flow CRUD |
| `*/api/quick-win-canvas/**` | D단계 - Quick Win CRUD |
| `*/api/build-win-canvas/**` | E단계 - Build Win CRUD |
| `*/api/funding/**` | F단계 - Impact Review |
| `*/api/teach/**` | 강사 수업/팀/제출물 관리 |
| `POST /api/teach/submission/rollback` | 팀의 특정 스테이지(A~F_BUILD/F_QUICK) 제출 상태 롤백 |
| `*/api/teach/report/**` | 팀 성과 리포트 |
| `*/api/admin/**` | 관리자 대시보드 |
> D/E단계는 `WinCanvasController`에서 `CanvasType`으로 분기하여 통합 처리


### 📄 API 문서 (Spring REST Docs)

배포 후 `/docs/index.html`에서 확인할 수 있습니다.

**빌드 흐름**
```
테스트 실행 → REST Docs 스니펫 생성 (build/generated-snippets)
           → Asciidoctor HTML 변환 (build/docs/asciidoc/index.html)
           → static/docs/index.html 로 복사 → 서버에서 /docs/index.html 노출
```

**업데이트 명령어**

```bash
# 1) HTML 재생성 (test → asciidoctor 자동 실행)
./gradlew asciidoctor

# 2) 서버가 서빙하는 위치로 복사
cp build/docs/asciidoc/index.html src/main/resources/static/docs/index.html
```

한 줄로:
```bash
./gradlew asciidoctor && cp build/docs/asciidoc/index.html src/main/resources/static/docs/index.html
```

> `bootJar` 시에는 `build.gradle:78-82` 설정에 의해 자동 복사되지만, 리포지토리에 `static/docs/index.html`을 커밋해 관리하므로 로컬에서 위 `cp` 명령으로 동기화해야 합니다.

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

> D/E단계 테스트는 동일한 `WinCanvasController`를 대상으로 합니다.

---

## 👥 User Roles

| 역할 | 설명 |
|------|------|
| 🎓 **STUDENT** | 팀에 소속되어 6단계 캔버스 작성/제출 |
| 👨‍🏫 **TEACHER** | 수업 생성, 팀 관리, 제출물 확인, 벌크 리포트 다운로드 |
| 🔑 **ADMIN** | 전체 수업 조회/관리 |

---

## 🗄 Database Schema

### ⚖ FK 없는 설계 — 의도와 정합성 보장 전략

이 프로젝트는 **DB 레벨 FK 제약을 의도적으로 두지 않습니다.** 모든 관계는 plain 타입 컬럼(`user_id`, `team_id` 등)으로만 표현되고, 조인은 애플리케이션 레이어에서 ID로 조회·조합합니다.

**왜 뺐나**

| 이유 | 설명 |
|------|------|
| JPA 연관관계의 부작용 회피 | N+1, `LazyInitializationException`, 양방향 매핑의 순환 직렬화 문제를 원천 차단 |
| 도메인 모듈 경계 보호 | `domain/`이 `storage/jpaentity`를 import하지 않도록 강제 — DIP 유지 |
| 스키마 진화 유연성 | 레거시 테이블(`tbgame`, `tbteam`, `tbmission` 등)과 신규 테이블이 공존하는 구조에서 FK는 부담 |

**그러면 정합성은 어떻게 보장하나**

1. **트랜잭션 경계는 Service에 단일 부착** — `@Transactional`이 Service에만 붙고, Implementation은 그 안에서 실행. 단일 Aggregate 단위의 쓰기는 모두 같은 트랜잭션 안에서 일어납니다.
2. **참조 검증은 Appender/Updater 진입 시점에** — 외래 ID는 사용 직전에 `existsById` / 조회로 검증, 없으면 `NotFoundException`.
3. **카운터성 데이터는 같은 트랜잭션에서 증감** — `tbteam.num_user`처럼 파생 카운터는 멤버 add/remove/move와 같은 트랜잭션에서 갱신해 일관성 유지.
4. **외부 I/O는 트랜잭션 밖으로** — OpenAI/파일 업로드 같은 긴 I/O는 트랜잭션 경계 바깥에서 실행해 락 보유 시간 최소화. 실패 시 보상 트랜잭션으로 처리.
5. **소프트 삭제 일관성** — `is_doing` / `status` 플래그로 소프트 삭제 후, 하위 참조는 같은 플래그로 동시에 비활성화.

**감수한 트레이드오프**

- DB 레벨 무결성은 포기 → 그 비용은 **애플리케이션 레이어 검증과 통합 테스트**(Spring REST Docs 기반)로 갚습니다.
- 운영 중 orphan 레코드 가능성 → 정합성 체크 배치/관리자 도구로 보완.

---

### 테이블 관계도 (논리적, FK 제약조건 없음 — 애플리케이션 레벨 조인)

```
 userinfo ─────┬──── identity_canvas        (B단계, user_id)
   │           ├──── impact_check            (A단계, user_id)
   │           ├──── flow_canvas             (C단계, user_id)
   │           │       ├── strategic_activity    (goal_id)
   │           │       └── tactical              (goal_id)
   │           ├──── win_canvas              (D/E단계, user_id + canvas_type)
   │           │       ├── task_activity         (canvas_id)
   │           │       ├── task_input            (canvas_id)
   │           │       ├── task_outcome          (canvas_id)
   │           │       └── teamwork              (canvas_id)
   │           ├──── f_letter_of_intent      (F단계, user_id + canvas_type)
   │           └──── logged_in               (세션, user_id)
   │
   └── teamuser ──── tbteam ──── tbgame
          (user_id,     (game_id,      ├── tbmission ── tbmissiondata
           team_id)      team_id)      └── contents
```

> DB에 FK 제약조건이 없으며, 모든 관계는 애플리케이션 코드에서 ID 값으로 조회합니다.

---

### 👤 사용자/인증

#### `userinfo` — 사용자 계정

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `user_id` | Long (PK, AI) | 사용자 고유 ID |
| `login_id` | VARCHAR(128), NOT NULL | 로그인 ID (학생: 팀코드 기반 자동 생성) |
| `password` | VARCHAR(128), NOT NULL | BCrypt 해시 비밀번호 |
| `user_name` | VARCHAR(128), NOT NULL | 사용자 이름 (학생은 login_id와 동일) |
| `code` | VARCHAR(128), NOT NULL | 수업 참여 코드 (tbgame.code와 매칭) |
| `user_role` | ENUM (STRING) | STUDENT / TEACHER / ADMIN |
| `writer` | VARCHAR(1) | 팀 대표작성자 여부 ("1" = 대표) |

#### `logged_in` — JWT 리프레시 토큰 세션

| 컬럼 | 타입 | 설명                   |
|------|------|----------------------|
| `logged_in_id` | Long (PK, AI) | 세션 고유 ID             |
| `user_id` | Long, NOT NULL | 사용자 FK               |
| `refresh_token` | VARCHAR(500) | JWT 리프레시 토큰(지금은 미사용) |
| `expired_at` | DATETIME | 토큰 만료 시간             |

#### `tbrole` — 역할 정의 (레거시)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `role_id` | Integer (PK, AI) | 역할 ID |
| `role_code` | VARCHAR(50) | 역할 코드 |
| `name` | VARCHAR(50) | 역할 이름 |
| `description` | VARCHAR(500) | 역할 설명 |

#### `userrole` — 역할 할당 (레거시)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `user_role_id` | Integer (PK, AI) | 할당 ID |
| `role_id` | Integer | 역할 FK |
| `is_doing` | Integer, NOT NULL | 활성 여부 |
| `powerlevel` | Integer, NOT NULL | 권한 레벨 |

---

### 📚 수업/팀 관리

#### `tbgame` — 수업 (게임)

| 컬럼 | 타입 | 설명                                             |
|------|------|------------------------------------------------|
| `game_id` | Integer (PK, AI) | 수업 고유 ID                                       |
| `name` | VARCHAR(100) | 수업 이름                                          |
| `code` | VARCHAR(32) | 수업 참여 코드 (학생 회원가입 시 사용)                        |
| `num` | Integer | 수업 번호                                          |
| `num_team` | Integer | 현재 팀 수                                         |
| `num_member` | VARCHAR(32) | 팀당 인원 수                                        |
| `created_at` | DATETIME | 생성일                                            |
| `ended_at` | DATETIME | 종료일                                            |
| `status` | Integer, NOT NULL | 수업 상태 (진행중 = 10, 준비중 = 1, 종료된 = 0, 100)        |
| `e_status` | Integer | 확장 상태                                          |
| `summary` | TEXT | 강의실 요약(현재는 쓰지않음)                               |
| `total_dd` | Integer, NOT NULL | 총 의사결정일 수                                      |
| `lang` | Integer | 언어 설정 (기본값 1, 안씀)                              |
| `world_type` | Integer | 수업 패키지 유형.('1' = 팀, '0' = 개인)                  |
| `step` | VARCHAR(100) | 현재 진행 단계 (A~F)                                 |
| `class_type` | VARCHAR(32) | 강의 타입. 1 = Premium, 2 = Basic, 3 = Start (미사용) |
| `is_doing` | Integer, NOT NULL | 활성 여부 (소프트 삭제). 1 = 활성, 0 = 삭제됨                |
| `reg_date` | DATETIME | 수업 등록 일시                                       |
| `popup_id` | Integer | 팝업 ID                                          |
| `image_url` | VARCHAR(255) | 수업 대표 이미지 URL                                  |
| `target` | VARCHAR(100) | 참여 대상                                          |
| `project_date` | VARCHAR(20) | 프로젝트 기간                                        |

#### `tbteam` — 팀

| 컬럼 | 타입 | 설명                            |
|------|------|-------------------------------|
| `team_id` | Integer (PK, AI) | 팀 고유 ID                       |
| `name` | VARCHAR(100) | 팀 이름 (예: "팀1", "팀2", "K컴퍼니")                          |
| `sequence` | Integer | 팀 순번 (1~6)                    |
| `status` | Integer | 팀 상태. 1 = 정상, -1 = 소프트 삭제     |
| `ai_play` | Integer |  AI 자동 플레이 여부. 0 = 실제 학생 팀, 1 = AI가 대신 플레이(미사용)                |
| `code` | VARCHAR(32) | 수업 코드 (tbgame.code와 동일)       |
| `is_doing` | Integer | 진행 중 여부(1 = 활성, 0 = 비활성)      |
| `team_category` | Integer | 팀 구분. 0 = 일반 사용자 팀, 1 = 평가자 팀 |
| `num_user` | Integer | 현재 팀원 수                       |

#### `teamuser` — 팀-사용자 매핑

| 컬럼 | 타입 | 설명                        |
|------|------|---------------------------|
| `team_user_id` | Integer (PK, AI) | 매핑 ID                     |
| `user_id` | Long | 사용자 FK                    |
| `team_id` | Integer | 팀 FK                      |
| `userlevel` | Integer, NOT NULL | 팀 내 사용자 권한 레벨 (기본값 5, 안씀) |
| `is_doing` | Integer, NOT NULL | 활성 여부. 1 = 활성, 0 = 비활성    |

#### `gameteam` — 수업-팀 매핑

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `game_id` | Integer (복합PK) | 수업 FK |
| `team_id` | Integer (복합PK) | 팀 FK |

#### `gameadmin` — 수업-강사 매핑

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `game_id` | Integer (복합PK) | 수업 FK |
| `user_id` | Long (복합PK) | 강사 FK |

#### `tbmission` — 미션

| 컬럼 | 타입 | 설명                          |
|------|------|-----------------------------|
| `mission_id` | Integer (PK, AI) | 미션 ID                       |
| `sequence` | Integer, NOT NULL | 미션 순서(게임 텀 내 순서)            |
| `subject` | TEXT | 미션 제목(미사용)                  |
| `summary` | TEXT | 미션 설명/요약(미사용)               |
| `startdate` | DATETIME | 미션 시작일                      |
| `enddate` | DATETIME | 미션 종료일                      |
| `dd_year` | Integer | 시뮬레이션 연차 (Decision Day Year) |
| `dd_term` | Integer | 시뮬레이션 분기/텀 (1~3)            |
| `mlevel` | Integer | 미션 활성 상태. (미사용)             |
| `game_id` | Integer | 수업 FK                       |
| `toinform` | VARCHAR(50) | 알림 수신 대상 (미사용)              |

#### `tbmissiondata` — 미션 진행 데이터

| 컬럼 | 타입 | 설명                |
|------|------|-------------------|
| `mission_data_id` | Integer (PK, AI) | 데이터 ID            |
| `team_id` | Integer, NOT NULL | 팀 FK              |
| `mission_id` | Integer, NOT NULL | 미션 FK             |
| `status_ceo` | Integer | CEO 역할 진행 상태(미사용) |
| `status_mar` | Integer | 마케팅 역할 진행 상태(미사용)     |
| `status_pro` | Integer | 생산 역할 진행 상태(미사용)       |
| `status_fin` | Integer | 재무 역할 진행 상태(미사용)       |
| `status_cho` | Integer | CHO 역할 진행 상태(미사용)      |

#### `contents` — 콘텐츠/파일 (현재는 미사용)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `contents_id` | Integer (PK, AI) | 콘텐츠 ID |
| `team_id` | Integer | 팀 FK |
| `game_id` | Integer | 수업 FK |
| `type` | Integer | 콘텐츠 유형 분류 (예: 9 = 사용자 파일)  |
| `subject` | VARCHAR(255) | 콘텐츠 제목 (예: "강의실 로고", "사용자 파일")  |
| `detail` | TEXT | 상세 내용 |
| `reg_date` | DATE | 등록 일시 (INSERT 시 now()) |
| `writer` | VARCHAR(100) | 작성자/업로더 이름 |
| `ext_dir` | VARCHAR(255) | 파일 저장 디렉토리 경로 (예: /cxinno/data/upload/user/file/) |
| `org_filename` | VARCHAR(255) | 원본 파일명 (예: "abl-logo.png", "자료.txt")   |
| `new_filename` | VARCHAR(255) | 저장된 해시 파일명 (예: "A9E598CBAB074DD085C96245C1FD3C70.png") |
| `status_type` | Integer |  상태. 1 = 활성, 0 = 삭제됨 (소프트 삭제) |
| `world_id` | Integer | 월드/강의실 참조. 강의실 로고일 경우 game_id 값, 일반 파일은 0 |

---

### 📊 A단계 - Impact Check

#### `impact_check` — 성과관리 현황 자가 진단

| 컬럼 | 타입 | 설명                        |
|------|------|---------------------------|
| `answer_id` | Long (PK, AI) | 답변 고유 ID                  |
| `q1_score` ~ `q12_score` | Integer | 객관식 12문항 점수 (각 1~5점)      |
| `q13_text` ~ `q16_text` | TEXT | 주관식 4문항 답변                |
| `user_id` | Long, NOT NULL, UNIQUE | 사용자 FK (1인 1회)            |
| `submitted` | Boolean (default false) | 제출 완료 여부(0 = 미제출, 1 = 제출) |

---

### 🧩 B단계 - Identity Canvas

#### `identity_canvas` — 조직 정체성 설계

| 컬럼 | 타입 | 설명             |
|------|------|----------------|
| `identity_id` | Long (PK, AI) | 캔버스 고유 ID      |
| `mission` | VARCHAR(255) | 미션             |
| `vision` | VARCHAR(255) | 비전             |
| `value` | VARCHAR(255) | 핵심가치           |
| `macro` | VARCHAR(255) | 정책/경제          |
| `tech` | VARCHAR(255) | 기술             |
| `customer` | VARCHAR(255) | 고객/사회          |
| `competitor` | VARCHAR(255) | 경쟁             |
| `capability` | VARCHAR(255) | 역량             |
| `culture` | VARCHAR(255) | 문화             |
| `structure` | VARCHAR(255) | 조직             |
| `etc` | VARCHAR(255) | 기타             |
| `new_mission` | VARCHAR(255) | new 미션         |
| `new_vision` | VARCHAR(255) | new 비전         |
| `new_value` | VARCHAR(255) | new 가치         |
| `user_id` | Long, NOT NULL, UNIQUE | 사용자 FK (1인 1개) |
| `submitted` | Boolean (default false) | 제출 완료 여부       |

---

### 📈 C단계 - Performance Flow

#### `flow_canvas` — 성과경로 설계

| 컬럼 | 타입 | 설명            |
|------|------|---------------|
| `goal_id` | Long (PK, AI) | 목표 고유 ID      |
| `goal_title` | VARCHAR(255) | 목표 제목         |
| `goal_description` | VARCHAR(255) | 존재 이유         |
| `order_no` | Integer, NOT NULL | 표시 순서(1, 2, 3까지) |
| `user_id` | Long, NOT NULL | 사용자 FK        |
| `submitted` | Boolean (default false) | 제출 완료 여부      |

#### `strategic_activity` — 전략적 활동 지표

| 컬럼 | 타입 | 설명                  |
|------|------|---------------------|
| `activity_id` | Long (PK, AI) | 활동 고유 ID            |
| `activity_metric` | VARCHAR(255) | 전략적 활동 지표           |
| `inter_criteria` | VARCHAR(255) | 내재화 기준              |
| `order_no` | Integer, NOT NULL | 표시 순서(1, 2, 3까지)    |
| `goal_id` | Long, NOT NULL | 목표 FK (flow_canvas) |

#### `tactical` — 전술적 지표

| 컬럼 | 타입 | 설명                  |
|------|------|---------------------|
| `metric_id` | Long (PK, AI) | 지표 고유 ID            |
| `tactical_metric` | VARCHAR(255) | 전술적 활동 지표           |
| `tactical_goal` | VARCHAR(255) | 전술 목표               |
| `order_no` | Integer, NOT NULL | 표시 순서(1, 2, 3까지)    |
| `goal_id` | Long, NOT NULL | 목표 FK (flow_canvas) |

---

### ⚡ D/E단계 - Quick Win & Build Win

#### `win_canvas` — Win 캔버스 (canvas_type으로 QUICK/BUILD 구분)

| 컬럼 | 타입 | 설명               |
|------|------|------------------|
| `canvas_id` | Long (PK, AI) | 캔버스 고유 ID        |
| `canvas_type` | ENUM('QUICK','BUILD') | 캔버스 유형           |
| `strategic_goal` | VARCHAR(128) | 전략 목표            |
| `task_name` | VARCHAR(255) | 실행 과제명 (=과제명)    |
| `task_description` | VARCHAR(255) | 주요 내용            |
| `crisis_signal` | VARCHAR(255) | 위기의 신호           |
| `pain_touch_point` | VARCHAR(255) | Pain/Touch Point |
| `user_id` | Long, NOT NULL | 사용자 FK           |
| `submitted` | Boolean (default false) | 제출 완료 여부         |

#### `task_activity` — 실행 활동 (win_canvas 하위)

| 컬럼 | 타입 | 설명               |
|------|------|------------------|
| `activity_id` | Long (PK, AI) | 활동 고유 ID         |
| `process_step` | VARCHAR(255) | 추진 절차(또는 전환 단계)  |
| `activity_content` | VARCHAR(255) | 주요 내용(또는 전환 활동)  |
| `duration` | VARCHAR(255) | 소요 기간            |
| `order_no` | Integer, NOT NULL | 표시 순서(1, 2, 3까지) |
| `canvas_id` | Long, NOT NULL | 캔버스 FK           |

#### `task_input` — 투입 자원 (win_canvas 하위)

| 컬럼 | 타입 | 설명    |
|------|------|-------|
| `input_id` | Long (PK, AI) | 투입 고유 ID |
| `resource_name` | VARCHAR(255) | 필요 자원 |
| `quantity` | Integer | 수량    |
| `order_no` | Integer, NOT NULL | 표시 순서(1, 2, 3까지) |
| `canvas_id` | Long, NOT NULL | 캔버스 FK |

#### `task_outcome` — 기대 성과 (win_canvas 하위)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `outcome_no` | Long (PK, AI) | 성과 고유 ID |
| `outcome_type` | ENUM('QUALITATIVE','QUANTITATIVE') | 성과 유형 |
| `outcome_content` | VARCHAR(255) | 성과 내용 |
| `order_no` | Integer, NOT NULL | 표시 순서(1, 2, 3까지) |
| `canvas_id` | Long, NOT NULL | 캔버스 FK |

#### `teamwork` — 팀워크 활동 (win_canvas 하위)

| 컬럼 | 타입 | 설명        |
|------|------|-----------|
| `teamwork_id` | Long (PK, AI) | 팀워크 고유 ID |
| `activity_teamwork` | VARCHAR(255) | 활동 팀워크    |
| `work_type` | VARCHAR(255) | 산출 팀워크    |
| `canvas_id` | Long, NOT NULL | 캔버스 FK    |

---

### 🧪 F단계 - Impact Review (펀딩 시뮬레이션)

#### `f_letter_of_intent` — 투자의향서 (canvas_type으로 BUILD/QUICK 구분)

| 컬럼 | 타입 | 설명                                 |
|------|------|------------------------------------|
| `intent_index` | Integer (PK, AI) | 의향서 고유 ID                          |
| `user_id` | Long, NOT NULL | 투자자 FK (작성한 학생)                    |
| `course_cd` | VARCHAR(32), NOT NULL | 과정 코드 (기본값 "QUICK")                |
| `category_cd` | VARCHAR(32), NOT NULL | 카테고리 코드 (기본값 "ID")                 |
| `investment_target` | VARCHAR(32) | 투자 대상 팀 ID (문자열)                   |
| `investment_price` | VARCHAR(100) | 투자 금액 (문자열, 가상 화폐)                 |
| `score1` ~ `score9` | Integer | 평가 점수 (문제인식, 해결방안, 확장성, 효과 등 9항목)  |
| `score10` | Integer | 예비 점수 (미사용)                        |
| `opinion` | TEXT | 투자 의견                              |
| `del_yn` | VARCHAR(8) | 삭제 여부 ("Y"/"N", soft delete) (미사용) |
| `reg_id` | VARCHAR(32) | 등록자 ID                             |
| `reg_dt` | DATETIME | 등록 일시                              |
| `mdfcn_id` | VARCHAR(32) | 수정자 ID (미사용)                       |
| `mdfcn_dt` | DATETIME | 수정 일시 (미사용)                        |
| `submitted` | Boolean, NOT NULL (default false) | 제출 완료 여부                           |
| `team_id` | Integer | 투자자 소속 팀 FK                        |
| `game_id` | Integer | 수업 FK                              |
| `canvas_id` | Long | 대상 win_canvas FK                   |
| `canvas_type` | ENUM('BUILD','QUICK'), NOT NULL | 캔버스 유형                             |

---

### 📦 기타 (레거시)

#### `tbapproval` — 승인

| 컬럼 | 타입 | 설명 |
|------|------|----|
| `approval_id` | Integer (PK, AI) | 승인 ID |
| `status` | Integer | 승인 상태. 0 = 미승인/대기, 1 = 승인 완료|
| `dd_year` | Integer | 시뮬레이션 연차 |
| `dd_term` | Integer | 시뮬레이션 분기 (1~3) |
| `level` | Integer | 미션/의사결정 단계 (0~6) |
| `role` | Integer | 역할. 1=CEO, 2=CMO, 3=COO, 4=CHO, 5=CFO, 6=CAO |
| `game_id` | Integer | 수업 FK |
| `team_id` | Integer | 팀 FK |

#### `tbstateapproval` — 상태 승인

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `state_approval_id` | Integer (PK, AI) | 상태 승인 ID |
| 나머지 | — | tbapproval과 동일 구조 |

#### `bc_missiongame` — 미션 게임 (부서별 미션)

| 컬럼 | 타입 | 설명         |
|------|------|------------|
| `bc_mission_game_id` | Integer (PK, AI) | 미션 게임 ID   |
| `ceo_mission` | VARCHAR(50) | CEO 미션 할당값 |
| `cmo_mission` | VARCHAR(50) | CMO 미션 할당값 |
| `coo_mission` | VARCHAR(50) | COO 미션 할당값 |
| `cho_mission` | VARCHAR(50) | CHO 미션 할당값 |
| `cfo_mission` | VARCHAR(50) | CFO 미션 할당값 |
| `d_year` | Integer | 시뮬레이션 연차   |
| `d_term` | Integer | 시뮬레이션 분기   |
| `game_id` | Integer | 수업 FK      |

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

### 🖥 서버 스펙

| 항목 | 상세 |
|------|------|
| 호스팅 | cafe24 (iptdesign.mycafe24.com) |
| OS | Rocky Linux |
| RAM | 1GB (944MB 가용) |
| Swap | 495MB |

### ⚙ JVM 설정

`/etc/systemd/system/impact.service`에서 JVM 옵션을 설정합니다.

```
ExecStart=/usr/bin/java -Xms256m -Xmx384m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=64m -Xss512k -jar /root/Impact_design-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --server.port=8080
```

| 옵션 | 값 | 설명 |
|------|----|------|
| `-Xms` | 256m | 초기 힙 크기 |
| `-Xmx` | 384m | 최대 힙 크기 (OOM 방지) |
| `-XX:+UseSerialGC` | - | 1코어 환경에서 G1GC보다 오버헤드 적음 |
| `-XX:MaxMetaspaceSize` | 128m | 클래스 메타데이터 메모리 상한 (비힙 폭증 방지) |
| `-XX:ReservedCodeCacheSize` | 64m | JIT 코드 캐시 기본값 240MB → 64MB로 절약 |
| `-Xss` | 512k | 스레드 스택 1MB → 512KB (스레드 30개 기준 ~15MB 절약) |

#### 메모리 사용 현황 (2026-04 기준)

| 프로세스 | 사용량 | 비고 |
|----------|--------|------|
| OS | ~150MB | |
| JVM (Spring Boot) | ~200-384MB | `-Xmx`로 힙 상한 고정 |
| JVM 비힙 (Metaspace, 스택, CodeCache) | ~80-100MB | MaxMetaspaceSize + Xss + CodeCache로 상한 고정 |
| MariaDB | ~22MB | |
| **합계** | **~450-656MB / 944MB** | 여유 있음 |

> 서버 메모리 확인: `free -h`
> 프로세스별 확인: `ps aux --sort=-%mem | head -5`

### 🧵 동시성 튜닝 (60 동접 타겟)

1코어·Heap 384MB 환경에서 수업 1개(팀 6 × 멤버 10 = **60명**) 동접을 안정 수용하는 것을 목표로 튜닝했습니다.

**`application-prod.yaml`**

| 설정 | 값 | 의도 |
|------|-----|------|
| `server.tomcat.threads.max` | 15 | 1코어 + I/O 대기 위주 요청 조합에 적정 |
| `server.tomcat.threads.min-spare` | 3 | 평상시 유휴 스레드 최소치 |
| `server.tomcat.accept-count` | **60** | 동시 제출 피크 흡수 (15 active + 60 queued = 75 수용) |
| `spring.datasource.hikari.maximum-pool-size` | 8 | 60 동접 기준 충분. 과도하게 키우면 메모리 압박 |
| `spring.datasource.hikari.connection-timeout` | 3000ms | 커넥션 고갈 시 빠르게 실패하여 스레드 점유 방지 |

**`application.yaml`**

| 설정 | 값 | 의도 |
|------|-----|------|
| `spring.servlet.multipart.max-file-size` | **10MB** | Heap 384MB에서 동시 업로드 OOM 방지 (기존 50MB → 축소) |
| `spring.servlet.multipart.max-request-size` | **10MB** | 동일 |

**`ExecutorConfig.java` — AI 전용 풀 (Tomcat 스레드 보호)**

| 설정 | 값 | 의도 |
|------|-----|------|
| corePoolSize / maxPoolSize | 6 / 6 | 동시 OpenAI 호출 상한 |
| queueCapacity | 20 | 작은 Heap에서 버스트 흡수 |
| RejectedExecutionHandler | `CallerRunsPolicy` | 큐 포화 시 자연스러운 백프레셔 (요청 유실 0) |

**실제 감당 범위**

| 부하 유형 | 60명 감당 여부 |
|---|---|
| 일반 조회/저장 | ✅ 여유 있음 (평균 12 req/s, Tomcat 15 스레드로 충분) |
| 로그인 피크 | ✅ 안전 |
| 동시 제출 | ✅ accept-count 60으로 흡수 |
| 리포트 생성 중 학생 접속 | ✅ ioExecutor 분리로 Tomcat 보호 |
| 동시 파일 업로드 | ✅ 10MB 제한으로 OOM 방지 |
| AI 분석 동시 요청 | ⚠️ 5~6명까진 안전, 초과 시 대기 |

> 수업을 **2개 이상 동시 운영(120명+)** 할 경우 RAM 증설(2GB+)이 필요합니다.

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
ssh root@iptdesign.mycafe24.com       # 서버 접속
systemctl status impact               # 상태 확인
sudo systemctl restart impact          # 재시작
sudo systemctl stop impact             # 중지
journalctl -u impact -n 50 --no-pager  # 최근 로그 50줄
journalctl -u impact -f                # 실시간 로그
```

---

## 🪪 License

Private
