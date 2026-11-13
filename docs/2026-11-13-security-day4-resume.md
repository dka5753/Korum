# 2026-11-13 학습 인수인계 — 4일차 CORS부터

이 문서는 Korum 로컬 학습용 실습 기록이다. 사용자는 설명과 작은 힌트 뒤 직접 코드를 작성하는 방식을 원한다.

## 현재 완료

- 401·403 JSON 응답, CSRF 예외별 메시지, 가입400·409 Map 응답 연결.
- React 수정·삭제에서 서버 오류 메시지 표시. 로그인 입력값으로 폼 요청, 성공 시 영역 닫기·입력 초기화, 로그인/로그아웃 버튼 조건부 표시.
- 초기 /me 조회로 세션에 맞는 화면 상태 복원. React Router로 / 게시판과 /signup 가입 화면 분리. 가입 완료 alert 확인 후 게시판 이동, 자동 로그인은 별도.
- 사용자 수동 확인: 가입 성공 및 새 계정 로그인, 잘못된 비밀번호401, 가입400·409의 화면 메시지, 수정·삭제403 메시지, 새로고침 로그인 표시 유지.
- 로그 확인: AuthorizationFilter URL 거절과 PreAuthorizeAuthorizationManager/AuthorizationManagerBeforeMethodInterceptor 메서드 거절 비교.
- XSS 관찰: 제목에 넣은 테스트 HTML 문자열이 글자로 표시되고 alert 미실행. 앱 전체의 XSS 안전성 검증은 아니다. 고정 글쓰기 제목은 원래 값으로 복원된 것을 파일에서 확인.
- 검증: npm run build 통과. 백엔드 compileKotlin --offline은 Gradle 로컬 소켓 생성 권한 제한으로 시작 실패, 컴파일 성공으로 기록하지 않는다. 서버 추가 실행이나 DB 변경은 하지 않았다.
- 남은 과제: 네트워크 예외 UI, 공통 CSRF 함수 정리, 글쓰기 입력 폼, 자동 권한 테스트. 페이지네이션은 사용자 요청으로 나중 과제로 보관.
- 다음 시작점: docs/2026-11-13-security-day4-resume.md. CORS 실험 결과는 아직 없음.

## 다음 첫 실습

백엔드가 실행된 상태에서 사용자 터미널로 실행:

```bash
curl -i http://localhost:8080/posts \
  -H "Origin: http://localhost:4000"
```

현재 WebConfig는 http://localhost:5173만 허용한다. 예상은403 및 Invalid CORS request이지만 아직 실행 결과는 확인하지 않았다. curl은 브라우저 CORS 정책을 강제하지 않으므로 여기서는 Spring 서버의 Origin 검사를 관찰한다. 이어 허용 Origin 요청과 비교하고, 브라우저 응답 읽기 제한·사전 요청을 구분한다.

CSRF는 계속 활성화되어 있다. 교안의 CSRF 비활성 시작 설정을 현재 프로젝트에 그대로 적용하지 않는다. XSS 교안의 dangerouslySetInnerHTML도 현재 게시판에 적용하지 않았다. CSP는 React HTML을 제공하는 서버의 응답에 적용되어야 한다는 점을 구분한다.
