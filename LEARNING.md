# Korum 학습 기록

이 문서는 Korum에서 진행한 학습용 구현과 관련 커밋을 기록한다. 각 기록은 당시 확인한 동작과 남아 있는 과제를 구분한다. 다음 학습에서도 날짜와 커밋을 추가해 이어간다.

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
