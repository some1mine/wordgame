# 초성게임 (Wordgame)

주어진 한글 초성에 맞는 단어를 제출하며 마지막까지 살아남는 멀티플레이 웹 게임입니다. 하나의 저장소에서 React 프론트엔드와 Spring Boot 백엔드를 함께 관리합니다.

## 주요 기능

- 회원가입 및 BCrypt 비밀번호 암호화
- Spring Security 세션 기반 로그인·로그아웃
- 게임방 생성 및 참가
- 국립국어원 한국어기초사전 API를 이용한 단어 확인
- 정답·오답에 따른 점수 계산과 탈락 처리
- 마지막 참가자의 승리 및 승패 전적 반영
- 서비스 단위 테스트와 전체 사용자 흐름 통합 테스트

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| 프론트엔드 | React 19, Create React App, Tailwind CSS, Lucide React |
| 백엔드 | Java 21, Spring Boot 3.3, Spring MVC, Spring Security |
| 데이터 | Spring Data JPA, Hibernate, H2 |
| 테스트 | JUnit 5, Mockito, AssertJ, MockMvc, React Testing Library |
| 빌드·배포 | Gradle Wrapper, npm, Docker, GitHub Actions |

## 프로젝트 구조

```text
wordgame/
├─ .github/workflows/   # 테스트 및 Docker Hub 이미지 게시
├─ backend/
│  ├─ Dockerfile
│  ├─ src/main/java/com/example/demo/
│  │  ├─ common/       # 보안 설정과 공통 유틸리티
│  │  ├─ controller/   # 사용자 및 게임 REST API
│  │  ├─ domain/       # JPA 엔티티와 게임 역할
│  │  ├─ dto/          # API 요청·응답 모델
│  │  ├─ repository/   # JPA 저장소
│  │  └─ service/      # 인증과 게임 진행 로직
│  └─ src/test/        # 단위·통합 테스트
└─ front/
   ├─ Dockerfile
   ├─ nginx.conf        # SPA 정적 파일 제공
   └─ src/             # React 화면과 테스트
```

## 사전 요구사항

- JDK 21
- Node.js 22 및 npm
- Docker (컨테이너 이미지 로컬 빌드 시)
- 국립국어원 한국어기초사전 API 인증키

## 환경변수 설정

백엔드 예시 파일을 복사해 로컬 `.env`를 만듭니다.

```powershell
Copy-Item backend\.env.example backend\.env
```

`backend/.env`에 실제 인증키를 입력합니다.

```properties
KOREAN_DICTIONARY_API_KEY=your-api-key
SESSION_COOKIE_SECURE=false
```

실제 `.env`는 Git에서 제외됩니다. HTTPS 운영 환경에서는 `SESSION_COOKIE_SECURE=true`를 사용해야 합니다.

## 실행 방법

### 백엔드

```powershell
cd backend
.\gradlew.bat bootRun
```

백엔드는 기본적으로 `http://localhost:8080`에서 실행됩니다.

### 프론트엔드

새 터미널에서 다음 명령을 실행합니다.

```powershell
cd front
npm ci
npm start
```

프론트엔드는 기본적으로 `http://localhost:3000`에서 실행됩니다. 로그인 후 브라우저가 받은 세션 쿠키는 이후 게임 API 요청에 자동으로 포함됩니다.

## Docker 실행

백엔드와 프론트엔드는 각각 독립된 이미지로 빌드됩니다. `.dockerignore`가 로컬 `.env`, 빌드 산출물과 의존성 디렉터리를 이미지 빌드 컨텍스트에서 제외합니다.

```powershell
docker build -t wordgame-backend:local .\backend
docker build --build-arg REACT_APP_API_BASE_URL=http://localhost:8080 -t wordgame-frontend:local .\front

docker run --rm -p 8080:8080 `
  -e KOREAN_DICTIONARY_API_KEY=your-api-key `
  -e SPRING_DATASOURCE_HIKARI_JDBC_URL=jdbc:h2:file:/data/word-initial-game `
  -v wordgame-data:/data `
  wordgame-backend:local

docker run --rm -p 3000:80 wordgame-frontend:local
```

`REACT_APP_API_BASE_URL`은 React 정적 파일을 만드는 시점에 포함되는 값입니다. 실제 서비스 주소로 이미지를 빌드해야 하며, 백엔드 API 인증키는 이미지에 넣지 않고 컨테이너 실행 환경변수로 전달합니다.

## GitHub Actions CI/CD

`.github/workflows/docker-publish.yml`은 다음 순서로 실행됩니다.

1. 백엔드 테스트와 프론트엔드 테스트·프로덕션 빌드를 병렬 실행합니다.
2. 두 검증이 성공하면 백엔드와 프론트엔드 Docker 이미지를 병렬 빌드합니다.
3. Pull Request에서는 이미지 빌드까지만 검증합니다.
4. `master` 푸시, `v*.*.*` 태그 또는 수동 실행에서는 Docker Hub에 이미지를 푸시합니다.

Docker Hub에서 `wordgame-backend`, `wordgame-frontend` 저장소를 만든 뒤 GitHub 저장소에 다음 설정을 추가합니다.

| 종류 | 이름 | 값 |
| --- | --- | --- |
| Actions secret | `DOCKERHUB_TOKEN` | Docker Hub에서 새로 발급한 Read & Write 액세스 토큰 |
| Actions variable (선택) | `DOCKERHUB_USERNAME` | 기본값 `thedeny1106`을 다른 네임스페이스로 바꿀 때 사용 |
| Actions variable (선택) | `REACT_APP_API_BASE_URL` | 프론트엔드 이미지에 포함할 백엔드 공개 URL |

게시되는 이미지 이름은 기본적으로 `thedeny1106/wordgame-backend`, `thedeny1106/wordgame-frontend`입니다. `master`에서는 `latest`, `master`, `sha-<커밋>` 태그를, `v1.2.3` 같은 Git 태그에서는 `1.2.3`, `1.2`, `sha-<커밋>` 태그를 생성합니다.

대화나 로그에 노출된 Docker Hub 토큰은 재사용하지 말고 Docker Hub에서 폐기한 다음 새 토큰만 `DOCKERHUB_TOKEN` Secret으로 등록해야 합니다.

## 게임 진행 방식

1. 사용자가 회원가입하고 로그인합니다.
2. 호스트가 초성, 방 이름, 정원을 지정해 게임방을 만듭니다.
3. 다른 사용자가 방에 참가합니다.
4. 참가 인원이 정원에 도달하면 게임이 시작됩니다.
5. 모든 참가자는 5점으로 시작합니다.
6. 올바른 초성과 사전에 존재하는 단어를 제출하면 1점을 얻고, 틀리면 1점을 잃습니다.
7. 점수가 0 이하가 된 사용자는 탈락하고 패배 횟수가 증가합니다.
8. 마지막 한 명이 남으면 게임이 종료되고 해당 사용자의 승리 횟수가 증가합니다.

## API 요약

회원가입과 로그인을 제외한 API는 로그인 세션이 필요합니다. 프론트엔드처럼 다른 출처에서 요청할 때는 쿠키 전달 옵션을 활성화해야 합니다.

### 사용자 API

| 메서드 | 경로 | 요청 | 설명 |
| --- | --- | --- | --- |
| `POST` | `/user/join` | `{ "userId", "password", "name" }` | 회원가입 |
| `POST` | `/user/login` | `{ "userId", "password" }` | 로그인 및 세션 생성 |
| `GET` | `/user/myinfo` | 없음 | 현재 로그인 사용자 조회 |
| `POST` | `/user/logout` | 없음 | 로그아웃 및 세션 무효화 |

### 게임 API

| 메서드 | 경로 | 요청 | 설명 |
| --- | --- | --- | --- |
| `GET` | `/game/all` | 없음 | 참가 가능한 게임 목록 |
| `GET` | `/game/get?gameId={id}` | 쿼리 파라미터 | 게임 상세 조회 |
| `POST` | `/game/make-game` | `{ "initial", "name", "capacity" }` | 게임방 생성 |
| `POST` | `/game/join-game` | `{ "gameId" }` | 게임 참가 |
| `POST` | `/game/submit` | `{ "gameId", "word" }` | 답안 제출 |
| `POST` | `/game/exit-game` | `{ "gameId" }` | 게임 나가기 |
| `POST` | `/game/end-if-need` | `{ "gameId" }` | 종료 조건 재확인 |

브라우저의 `fetch`를 직접 사용하는 경우 세션 쿠키 전달을 위해 다음 옵션을 포함합니다.

```javascript
fetch('http://localhost:8080/game/all', {
  credentials: 'include',
});
```

## 테스트

### 백엔드

```powershell
cd backend
.\gradlew.bat test
```

테스트 프로필은 파일 DB 대신 메모리 H2를 사용합니다. 회원가입, 로그인, 세션 인증, 방 생성, 게임 참가, 답안 제출, 승패 반영과 종료 흐름을 검증합니다.

### 프론트엔드

```powershell
cd front
$env:CI='true'
npm test -- --watchAll=false --runInBand
npm run build
```

## 참고

- 로컬 CORS 허용 출처는 `http://localhost:3000`입니다.
- 로그인 세션의 기본 유효 시간은 30분입니다.
- 게임방 정원은 2명 이상 20명 이하입니다.
