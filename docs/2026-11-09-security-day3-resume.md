# Spring Security 3일차 — 집에서 이어하기

저장일: 2026-11-09. 3일차 전체 완료가 아니라 메서드 보안·소유자 검사까지 진행했다.

## 완료한 내용

1. 글쓰기 요청 DTO에서 author 제거. 서버의 Principal.name을 작성자로 저장.
2. 서비스 if문으로 작성자 검사 후, PostGuard와 PreAuthorize로 전환.
3. 수정·삭제 조건: `@postGuard.isAuthor(#id, authentication.name) or hasRole('ADMIN')`.
4. 관리자 URL은 authenticated, AdminController 클래스에 ADMIN 메서드 보안 적용.
5. 수정·삭제에서 필요 없어진 username/Principal 인자 제거. 글쓰기 Principal은 유지.
6. React 글쓰기·수정·삭제 요청과 CSRF 연결. 표, 즉시 목록 갱신, 로그인·로그아웃·권한 오류 메시지 추가.

## 중요한 개념

- Principal은 서버의 인증 정보에서 전달된다. 직접 구현한 인터페이스는 UserDetailsService이며 loadUserByUsername을 override했다.
- Kotlin의 principal.name은 Java의 getName()을 호출하는 프로퍼티 접근 문법이다.
- 인증 여부와 소유자 검사는 다르다. 401은 인증 필요, 403은 접근 거절이다. CSRF 오류도403일 수 있다.
- 메서드 보안은 본문 실행 전에 조건을 검사한다. EnableMethodSecurity가 기능을 켜고 PreAuthorize가 조건을 붙인다.
- PostGuard는 DB 작성자와 authentication.name을 비교한다. OR 조건으로 관리자를 추가 허용한다.
- 서버 검사는 필수다. 버튼을 숨기는 것만으로 직접 HTTP 요청을 막을 수 없다.
- 자료 5일차의 복수 체인은 JWT 경로와 세션 경로를 나누는 용도다. 로그아웃 화면 추가나 메서드 보안 활성화 때문에 체인을 늘리지 않는다.

## 실제 확인과 검증 범위

사용자 수동 확인: 작성201·로그인 작성자 저장, 본인 수정·삭제 성공, 관리자 API 일반 사용자403/관리자 성공, 관리자 남의 글 수정·삭제 성공, 다른 일반 사용자 수정·삭제403. 관리자 허용으로 전환한 후 본인 일반 사용자 성공은 다시 확인하면 좋다.

인자 정리 후 백엔드 compileKotlin, 저장 전 프런트 npm run build 통과. 자동 통합 테스트는 수행하지 않았다.

해결한 오류: map 밖에서 post 참조 → 버튼 이동, 수정 메서드 POST → PUT으로 바꿔405 해결. 상태는 setPosts로 갱신하며 작성은 배열 추가, 수정은 map, 삭제는 filter를 사용한다.

## 다음은 자료 3일차 4단계

**401·403을 JSON 응답으로 통일하기**부터 시작한다. 현재 SecurityConfig의 authenticationEntryPoint는 상태401만 설정한다. accessDeniedHandler는 아직 별도 설정하지 않았다.

다음 대화의 첫 질문:

> 로그인하지 않은 요청의401과 로그인했지만 권한이 없는 요청의403은 왜 구분해서 안내해야 할까?

답을 확인한 뒤 작은 단계로 진행한다.

1. 자료 `practice/09_03/09_03_실습_튜토리얼.md`의 4단계를 다시 읽는다.
2. authenticationEntryPoint와 accessDeniedHandler의 역할을 구분한다.
3. JSON Content-Type과 오류 본문을 구성하고 프런트에서 읽는다. HTML·빈 본문을 무조건 JSON으로 읽지 않는다.
4. Korum 보충: CSRF 보호가 켜져 있으므로 권한 부족403과 CSRF403을 구분한다. 현재 프런트의 “본인 글만” 메시지는 모든403을 같은 이유로 간주하므로 개선한다.
5. 자료 5단계의 URL 거절·메서드 거절 로그를 비교한다. 이후4일차로 진행한다.

아직 하지 않은 작업: 사용자 입력 로그인/가입/글쓰기/수정 폼, 새로고침 후 username 복원, 네트워크 예외 UI, 자동 권한 테스트. 없는 글에 대한 일반 사용자403/관리자404 차이도 정책 결정 후 정리한다.

## 집에서 실행하기

1. 다른 컴퓨터라면 먼저 `git status`로 로컬 변경 확인 후 `git pull --ff-only`.
2. `.env`는 Git에 없다. 기존 로컬 DB와 맞는 DB_ROOT_PASSWORD, DB_PASSWORD를 준비한다. DB 데이터와 계정도 Git으로 이동하지 않는다. 기존 볼륨을 임의로 삭제하지 않는다.
3. Docker Desktop 실행 후 저장소 루트에서 `docker compose up -d mariadb`.
4. IntelliJ에서 KorumApplication.kt의 main 실행. DB_PASSWORD는 해당 컴퓨터의 DB 비밀번호로 설정하고 Java21을 사용한다.
5. 프런트 폴더에서 의존성이 없다면 `npm ci`, 이후 `npm run dev`. CORS 허용 주소는 localhost:5173.
6. 백엔드8080·DB3307·프런트를 함께 실행한 상태에서 실습한다. 고정 로그인 아이디는 App.tsx에서 확인한다. 계정 전환 전 로그아웃하고 환영 메시지 또는 /me로 실제 사용자를 확인한다.

## 다음 대화에 붙일 문장

> Korum 3일차 이어서 하자. 작성자는 Principal로 저장하고, 수정·삭제는 PostGuard와 PreAuthorize로 본인 또는 관리자만 허용했어. 관리자 성공과 다른 일반 사용자403을 확인했고 인자 정리도 끝났어. 다음은401·403 JSON 응답과 거절 로그 비교야. 작은 힌트를 주면 내가 직접 작성하는 방식으로 진행해줘.
