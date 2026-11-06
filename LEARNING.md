# Korum 학습 기록

이 문서는 Korum에서 진행한 학습용 구현과 관련 커밋을 기록한다. 각 기록은 당시 확인한 동작과 남아 있는 과제를 구분한다. 다음 학습에서도 날짜와 커밋을 추가해 이어간다.

## 2026-11-07 — React 세션 로그인·로그아웃 완료

학습은 11월 6일 밤부터 7일 새벽까지 진행했다. **2일차 실습 완료, 3일차는 개념 설명까지만 진행**했다.

### 구현한 것

- 폼 로그인 처리 주소 `/api/login`, 성공200·실패401 설정.
- 로그아웃 주소 `/api/logout`, 기본 세션 무효화 후 성공200 설정.
- React에서 CSRF 토큰을 받고 폼 인코딩으로 로그인. `/me`는 Basic 헤더 대신 쿠키를 포함한다.
- 로그아웃 직전 CSRF 토큰을 다시 받아 POST 요청을 보낸다.
- 실제 요청에 남아 있던 Basic 인증의 영향을 확인하고 서버의 Basic 인증을 비활성화했다.
- 미인증 API 접근에 `authenticationEntryPoint`로 401을 반환해 로그인 HTML의 JSON 파싱 오류를 해결했다.

### 확인한 결과

사용자 제공 브라우저 로그에서 **로그인200 → user 조회 → 로그아웃200 → 조회401**을 확인했다. 로그인 전후 세션 ID가 달라짐과 `Securing POST /api/login`, `Securing GET /me` 로그는 사용자 확인 기준이다. 세션 ID와 DB 비밀번호 원문은 저장하지 않았다.

저장 전 `npm run build`, `gradlew.bat compileKotlin`, `git diff --check`가 통과했다. 자동 통합 테스트는 실행하지 않았다. 잘못된 비밀번호의 폼 로그인401은 설정만 확인했으며 이번 실습에서 따로 재검증하지 않았다.

### 배운 점과 해결한 오류

세션의 로그인 정보와 서버 연결을 구분했다. BCrypt는 무작위 솔트로 해시를 생성하므로 새 encode 결과를 직접 비교하지 않고 matches로 검증한다. 로그인 이후에는 세션 인증 정보를 사용한다. 세션 ID 교체는 DB 사용자 ID 변경이 아니라 세션 고정 공격 방어다.

함수명 `handlelogin` 오타, 사용하지 않는 Basic headers 변수, 백엔드 미실행의 연결 거절, DB 접속 환경 변수 설정을 확인했다. 로그아웃 후 401은 정상 차단이지만 React의 미처리 Promise 오류가 Console에 표시되는 UI 개선 과제는 남아 있다.

기존 작업에 있던 Actuator 의존성 추가와 npm 잠금 파일 peer 메타데이터 변경도 함께 저장했다. Actuator 별도 검증은 수행하지 않았다. `.env`는 Git에서 제외했다.

### 다음 시작점

`authenticated()`는 본인 글을 보장하지 않는다는 점과 요청의 author를 믿지 않고 서버 로그인 정보로 작성자를 정해야 한다는 점을 설명했다. **CreatePostRequest.author 제거, Principal 기반 작성자 저장, 소유자 검사는 아직 구현하지 않았다.**

다음 질문부터 시작한다: “DB 작성자가 user인 글을 kim이 삭제하려 한다면 어떤 두 값을 비교해야 할까?” 답변을 확인한 뒤 작은 힌트로 직접 작성하는 방식을 유지한다.

[복습·오류 해결·다음 실습 순서](docs/2026-11-07-security-day2-summary-day3-resume.md)

## 2026-11-05 — 회원가입과 CSRF 요청 연결

**구현 커밋:** [`9b5b43b`](https://github.com/dka5753/Korum/commit/9b5b43b) — `feat: CSRF 토큰 기반 회원가입과 입력·중복 검증 구현`

### 직접 구현한 것

- SignupService에서 공백·실습용 비밀번호 길이 검사, 사용자 이름 중복 검사, BCrypt 해시 생성 후 USER 역할로 DB 저장.
- InvalidSignupException과 DuplicateUsernameException을 각각 400·409로 응답하도록 전역 처리.
- POST `/signup` 컨트롤러와 본문 없는 201 응답, 로그인 전 접근 허용.
- GET `/csrf`에서 Spring Security 토큰을 받아 반환하고 공개 조회 허용.
- WebConfig에서 프런트 Origin `http://localhost:5173`과 쿠키 포함 CORS 요청 허용.
- React의 getCsrf → credentials include·CSRF 헤더 포함 회원가입 POST → 상태 출력 연결.
- 편집기 생성 `kls_database.db`는 로컬 캐시이므로 Git 제외. 기존 서비스·예외 일부의 서식 변경도 포함.

### 확인한 결과

| 조건 | 결과 |
|---|---|
| 새 아이디 회원가입 | 201 (사용자 확인) |
| 같은 아이디 재가입 | 409 (사용자 확인) |
| 빈 아이디 | 400 (사용자 명시 확인) |
| 가입 계정으로 브라우저 폼 로그인 후 `/me` | 해당 사용자 JSON 응답 (사용자 확인) |

작업 저장 전 `compileKotlin`과 `npm run build`가 성공했다. Kotlin 컴파일은 UP-TO-DATE 결과였고, 환경의 FSEvents 경고가 있었지만 종료 코드는 0이었다. API 검증은 수동 실습 보고 기준이며 자동 통합 테스트를 실행한 것은 아니다.

### 개념과 오류 해결

쿠키는 브라우저가 보관하고 요청 대상과 전송 조건에 따라 첨부한다. 서버 세션과 쿠키의 역할을 구분했다. CSRF 토큰은 세션 쿠키와 별개로 요청 헤더에 제출하며, 프런트·백엔드 분리나 비밀번호 해시와 다른 개념이다.

DB 미기동으로 localhost:3307 Connection refused가 발생했고, DB 준비 후 `/csrf` 응답을 확인했다. Kotlin 언어 서버의 내부 강조 오류와 실제 Gradle 컴파일 결과도 구분했다. 언어 서버 오류는 완전히 해결됐다고 검증하지 않았다.

### 다음 시작점

**회원가입은 동작하지만 React 세션 로그인·로그아웃 요청은 아직 구현 전이다.** 다음은 formLogin을 `/api/login` 처리와 성공200·실패401 응답으로 구성하는 단계부터 시작한다. 기존 Basic `/me` 버튼은 유지되어 있다.

[이어하기 가이드](docs/2026-11-04-day2-resume.md)의 A~C 주요 구현은 완료했다. 다음 실습은 D 단계다. 고정 입력값·4자 기준·초기화 계정은 학습용이며 이메일 형식 검사, DB 고유 제약과 동시 가입 처리, 사용자 입력 UI는 후속 보완 범위다.

## 2026-11-04 — Spring Security 2일차 진행 상태

**작업 저장:** `wip: BCrypt 및 DB 로그인 실습과 회원가입 작성 상태 저장`

### 완료한 부분

- BCrypt 검증기 등록, 평문 형식 불일치의 401과 해시 적용 후 로그인 성공 비교.
- User의 password·role, UserRepository, JpaUserDetailsService 작성.
- 메모리 계정 빈 제거, DB user/admin 계정이 없을 때만 BCrypt 해시로 초기화.
- 사용자 보고로 DB 계정의 내 정보 응답과 역할별 접근 비교 확인.
- SignupRequest와 SignupService의 생성자·빈 signup 함수 작성.

### 작성 중인 부분

**SignupService.signup은 아직 빈 함수이며 회원가입 기능은 동작하지 않는다.** 입력 검증·중복 검사·계정 저장·컨트롤러가 남았다. React 세션 로그인·로그아웃 요청과 CSRF 토큰 연결도 아직 구현하지 않았다.

### 환경과 검증

Spring Security 7.1.1의 encode 반환 선언에 맞춰 저장할 해시를 checkNotNull로 확인했다. 작업 저장 전 누락 import를 보완했고 `compileKotlin`이 통과했다. Kotlin daemon 경로의 환경 쓰기 제한 경고 후 daemon 없는 fallback 컴파일로 성공했다. 자동 테스트나 이번 저장 시점의 서버 재실행은 수행하지 않았다.

### 집에서 이어가기

[2일차 과제·힌트](docs/2026-11-04-day2-resume.md)의 A 단계에서 SignupService 본문부터 작성한다. 가이드는 원본 교안의 CSRF 임시 비활성화와 구분하여 보호를 유지하는 추가 연결 경로를 담았다.

## 2026-11-03 — Spring Security 1일차

**구현 커밋:** `91c6514` — `feat: Spring Security 인증 및 역할별 접근 제어 실습`

### 목적과 범위

기존 게시판에 Spring Security를 적용해 인증·인가·필터 체인과 React 인증 요청을 학습했다. 아래 내용은 **로컬 수업용 구현**이며, 실제 서비스용 로그인 기능을 완성한 상태는 아니다.

- 백엔드: Kotlin, Spring Boot 4.1.1, Java 21
- 프런트: React, TypeScript, Vite
- 로컬 주소: 프런트 `http://localhost:5173`, 백엔드 `http://localhost:8080`
- 학습 방식: 설명과 힌트를 바탕으로 직접 작성하고, 실행 결과를 비교했다.

### 커밋에 포함한 변경

| 파일 | 변경 내용 |
|---|---|
| `korum/build.gradle` | Spring Security 의존성 추가 |
| `korum/gradlew` | 실행 권한 추가 |
| `korum/src/main/kotlin/com/example/korum/config/SecurityConfig.kt` | 요청별 접근 규칙, 폼 로그인, HTTP Basic, CORS, 메모리 계정 설정 |
| `korum/src/main/kotlin/com/example/korum/controller/MeController.kt` | 로그인 사용자 이름을 JSON으로 반환하는 GET `/me` 추가 |
| `korum/src/main/kotlin/com/example/korum/controller/AdminController.kt` | 역할 비교를 위한 실습용 GET `/admin/check` 추가 |
| `korum/src/main/kotlin/com/example/korum/controller/PostController.kt` | CORS 허용 프런트 포트를 5173으로 수정하고 코드 정렬 |
| `korum/src/main/resources/application.properties` | 보안 필터 흐름 관찰을 위한 TRACE 로깅 추가 |
| `korum-frontend/src/App.tsx` | Basic 인증 헤더를 담은 `/me` 요청, 실행 버튼 및 콘솔 출력 추가 |

### 접근 규칙

| 요청 | 접근 조건 |
|---|---|
| GET `/posts`, `/posts/**` | 누구나 허용 |
| `/admin/**` | `ADMIN` 역할 필요 |
| 그 외 요청 | 로그인 필요 |

`authenticated()`는 로그인 여부를 검사하고, `hasRole("ADMIN")`은 관리자 역할을 검사한다. 아이디 이름이 아니라 부여된 역할로 접근을 결정한다.

### 직접 확인한 동작

- 기본 로그인 화면과 폼 로그인 성공.
- `/me`에서 고정 문자열 반환 후 `Principal.name`으로 사용자 이름 반환.
- `Map<String, String>`을 반환해 `{"username":"user"}` JSON 응답 확인.
- 메모리 관리자 계정으로 `/admin/check` 접근 성공.
- 일반 사용자 계정으로 관리자 API 접근 시 403 확인.
- React에서 올바른 Basic 인증으로 `/me` 호출 성공, 잘못된 비밀번호로 401 비교.
- CORS 수정 후 Network에서 `posts` 및 `me` 요청의 200 응답 확인.
- 요청 헤더의 `GET /me`, `Accept: application/json`, `Origin: http://localhost:5173` 확인.
- TRACE 로그에서 로그인 화면 필터 처리, 인증된 일반 사용자 정보, 403 응답과 `/error` 후속 처리 관찰.

검증은 수동 실행, 브라우저 화면, 콘솔 및 서버 로그를 기준으로 했다. 이 커밋에서 자동 테스트를 실행해 통과를 확인한 것은 아니다. OPTIONS 사전 요청과 응답의 `Access-Control-Allow-Origin` 헤더 자체는 직접 관찰하지 않았다.

### 학습 중 해결한 문제

| 현상 | 확인한 원인과 수정 |
|---|---|
| 공개 규칙을 작성했지만 로그인 화면으로 이동 | `SecurityConfig`의 `@Configuration` 누락 수정 |
| React `/me` 요청 401 | `Authorization`의 `Basic` 뒤 공백 누락 수정 |
| React `/posts` 요청 CORS 403 | 허용 Origin의 5174를 실제 프런트 포트 5173으로 수정 |
| `gradlew` 실행 권한 거절 | 프로젝트 위치를 확인하고 실행 권한 추가 |
| Vite 실행 파일 없음 | 로컬 프런트 의존성 설치 안내 후 개발 서버 실행 확인 |
| DB 연결·인증 실패 | DB 기동과 실행 환경의 `DB_PASSWORD` 전달 여부 점검 후 실행 재개; 최종 복구 경로 자체는 별도 확인하지 않음 |

### 학습용 설정과 아직 구현하지 않은 부분

- `InMemoryUserDetailsManager`의 계정은 메모리 실습 계정이다. DB의 사용자 엔티티와 로그인은 아직 연결하지 않았다.
- `{noop}`와 고정 실습 비밀번호는 인증 흐름을 이해하기 위한 설정이다. BCrypt 기반 비밀번호 저장·검증은 후속 학습에서 구현한다.
- 프런트의 고정 Basic 인증 정보는 로컬 요청 실습용이다. 사용자 입력 로그인 UI, 자격 증명 처리 및 HTTPS를 포함한 실제 운영 인증 구성을 구현한 것은 아니다. Base64는 암호화가 아니다.
- TRACE는 필터 관찰용이다. 로그가 많고 인증 관련 정보가 포함될 수 있으므로 실습 후 필요한 로그 수준으로 조정한다. 이 기록에 세션 값이나 실제 DB 비밀번호를 남기지 않는다.
- 작성·수정·삭제 요청에는 현재 인증 규칙이 적용되지만, CSRF 처리와 프런트의 쓰기 요청 연결은 아직 실습하지 않았다. 로그인만 하면 글 작성이 완성된 상태라고 보지 않는다.
- 게시글 작성자 자동 지정, 본인 글만 수정·삭제하는 소유자 검사는 아직 구현하지 않았다.
- 프런트 요청 실패 시 사용자에게 보여줄 오류 UI와 예외 처리는 보완 과제로 남아 있다.

### 다음 학습과 목표

- [ ] BCrypt를 적용한 비밀번호 검증
- [ ] DB 사용자 기반 로그인
- [ ] 로그인한 사용자만 게시글 작성 가능하도록 프런트·백엔드 연결
- [ ] 로그인 정보로 게시글 작성자 자동 지정
- [ ] 작성자 본인만 수정·삭제하도록 소유자 검사
- [ ] OPTIONS 및 CORS 응답 헤더 직접 관찰
- [ ] 접근 규칙을 검증하는 자동 테스트 추가

## 이후 기록 방식

후속 구현을 커밋한 뒤 아래 항목을 새 날짜 아래에 추가한다.

- 관련 커밋과 학습 목표
- 변경한 파일과 기능
- 실제 확인한 결과와 검증 방법
- 학습용 임시 설정, 아직 확인하지 않은 부분
- 다음에 이어갈 과제
