# 프론트엔드 독립 개발 환경 가이드

이 가이드는 프론트엔드 팀이 백엔드 팀의 도움 없이 독립적으로 개발하고 테스트할 수 있도록 도와줍니다.

---

## 📋 목차

1. [솔루션 비교](#솔루션-비교)
2. [방법 1: Docker를 이용한 백엔드 실행 (추천)](#방법-1-docker를-이용한-백엔드-실행-추천)
3. [방법 2: Mock Service Worker (MSW)](#방법-2-mock-service-worker-msw)
4. [방법 3: JSON Server](#방법-3-json-server)
5. [API 문서 확인 방법](#api-문서-확인-방법)
6. [주요 API 엔드포인트](#주요-api-엔드포인트)

---

## 솔루션 비교

| 방법 | 장점 | 단점 | 추천 상황 |
|------|------|------|-----------|
| **Docker** | ✅ 실제 데이터 사용<br>✅ 완전한 API 기능<br>✅ 백엔드와 동일한 환경 | ❌ Docker 설치 필요<br>❌ 리소스 사용량 높음 | 실제 데이터로 테스트가 필요할 때 |
| **MSW** | ✅ 빠른 개발<br>✅ 오프라인 작업 가능<br>✅ 네트워크 제어 | ❌ 초기 설정 필요<br>❌ 데이터 직접 작성 | API 응답만 확인하면 될 때 |
| **JSON Server** | ✅ 설정 매우 간단<br>✅ 빠른 시작 | ❌ 기능 제한적<br>❌ 복잡한 쿼리 불가 | 빠른 프로토타이핑 |

---

## 방법 1: Docker를 이용한 백엔드 실행 (추천)

Docker를 사용하면 프론트엔드 개발자도 백엔드 애플리케이션을 쉽게 실행할 수 있습니다.

### 1-1. 사전 준비

#### Docker Desktop 설치

**Windows:**
1. [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/) 다운로드
2. 설치 후 재시작
3. Docker Desktop 실행 확인

**Mac:**
1. [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop/) 다운로드
2. 설치 후 실행

### 1-2. 프로젝트 파일 받기

```bash
# Git에서 프로젝트 클론 (프론트엔드 폴더와 별도 위치에)
git clone [repository-url] passbee-backend
cd passbee-backend
```

### 1-3. Docker Compose 파일 생성

프로젝트 루트에 `docker-compose.yml` 파일을 생성합니다:

```yaml
version: '3.8'

services:
  # MySQL 데이터베이스
  mysql:
    image: mysql:8.0
    container_name: passbee-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: passbee
    ports:
      - "3307:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Spring Boot 애플리케이션
  app:
    build: .
    container_name: passbee-api
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/passbee?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpassword
      DB_PASSWORD: rootpassword
      JWT_SECRET: dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tZ2VuZXJhdGlvbi1taW5pbXVtLTY0LWJ5dGVzLWxvbmc=
      QNET_SERVICE_KEY: your-qnet-service-key-here
    ports:
      - "8080:8080"
    volumes:
      - ./:/app
    restart: unless-stopped

volumes:
  mysql-data:
```

### 1-4. Dockerfile 생성

프로젝트 루트에 `Dockerfile`을 생성합니다:

```dockerfile
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 1-5. Docker 실행

```bash
# Docker 컨테이너 시작 (최초 실행 시 10-15분 소요)
docker-compose up -d

# 로그 확인
docker-compose logs -f app

# 애플리케이션이 완전히 시작될 때까지 대기 (1-2분)
# "Started All4uServerApplication" 메시지가 나오면 준비 완료
```

### 1-6. 백엔드 접속 확인

브라우저에서 다음 URL에 접속:
- **API 문서 (Swagger)**: http://localhost:8080/swagger-ui.html
- **자격증 목록 API**: http://localhost:8080/api/licenses

### 1-7. 초기 데이터 수집 (선택)

Swagger UI에서 다음 API를 순서대로 실행하여 데이터를 수집합니다:

1. **Qualifications** > `GET /api/qualifications/import` - 자격증 종목 수집
2. **Licenses** > `POST /api/admin/licenses/sync` - License 테이블 동기화
3. **Agencies** > `POST /api/agencies/import` - 기관 정보 수집

### 1-8. Docker 관리 명령어

```bash
# 컨테이너 중지
docker-compose stop

# 컨테이너 시작 (이미 생성된 경우)
docker-compose start

# 컨테이너 삭제 (데이터는 유지)
docker-compose down

# 컨테이너 및 데이터 완전 삭제
docker-compose down -v

# 상태 확인
docker-compose ps
```

### 1-9. VS Code에서 프론트엔드 개발

이제 프론트엔드 프로젝트에서 백엔드 API를 호출할 수 있습니다:

```javascript
// fetch 예제
fetch('http://localhost:8080/api/licenses')
  .then(response => response.json())
  .then(data => console.log(data));

// axios 예제
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// 자격증 목록 조회
const licenses = await api.get('/licenses');

// 검색
const searchResults = await api.get('/search?query=정보처리');

// 로그인
const loginResponse = await api.post('/auth/login', {
  email: 'test@example.com',
  password: 'password123'
});
```

---

## 방법 2: Mock Service Worker (MSW)

MSW를 사용하면 네트워크 요청을 가로채서 가짜 응답을 반환할 수 있습니다.

### 2-1. MSW 설치

프론트엔드 프로젝트 폴더에서:

```bash
npm install msw --save-dev
# 또는
yarn add msw --dev
```

### 2-2. MSW 초기화

```bash
npx msw init public/ --save
```

### 2-3. Mock 핸들러 생성

`src/mocks/handlers.js` 파일 생성:

```javascript
import { http, HttpResponse } from 'msw'

export const handlers = [
  // 자격증 목록 조회
  http.get('http://localhost:8080/api/licenses', () => {
    return HttpResponse.json([
      {
        jmcd: "1320",
        jmfldnm: "정보처리기사",
        seriescd: "01",
        seriesnm: "기사",
        qualgbcd: "T",
        qualgbnm: "국가기술자격",
        summary: "컴퓨터 정보처리 기술에 관한 자격",
        job: "소프트웨어 개발, 데이터베이스 관리 등",
        career: "IT 분야 취업 및 진로",
        trend: "알고리즘, 데이터베이스, 네트워크 등 출제",
        createdAt: "2024-01-01T00:00:00"
      },
      {
        jmcd: "2290",
        jmfldnm: "정보보안기사",
        seriescd: "01",
        seriesnm: "기사",
        qualgbcd: "T",
        qualgbnm: "국가기술자격",
        summary: "정보보안에 관한 자격",
        job: "보안 시스템 구축 및 운영",
        career: "정보보안 전문가",
        trend: "암호학, 네트워크 보안 등",
        createdAt: "2024-01-01T00:00:00"
      }
    ])
  }),

  // 자격증 검색
  http.get('http://localhost:8080/api/licenses/search', ({ request }) => {
    const url = new URL(request.url)
    const keyword = url.searchParams.get('keyword')
    
    return HttpResponse.json([
      {
        jmcd: "1320",
        jmfldnm: "정보처리기사",
        seriescd: "01",
        seriesnm: "기사",
        qualgbcd: "T",
        qualgbnm: "국가기술자격",
        createdAt: "2024-01-01T00:00:00"
      }
    ])
  }),

  // 통합 검색
  http.get('http://localhost:8080/api/search', ({ request }) => {
    const url = new URL(request.url)
    const query = url.searchParams.get('query')
    
    return HttpResponse.json([
      {
        type: "LICENSE",
        id: "1320",
        title: "정보처리기사",
        content: "기사",
        createdAt: "2024-01-01T00:00:00"
      },
      {
        type: "REVIEW",
        id: "1",
        title: "Re: 정보처리기사",
        content: "실기 시험이 어려웠습니다...",
        createdAt: "2024-01-15T10:30:00"
      }
    ])
  }),

  // 로그인
  http.post('http://localhost:8080/auth/login', async ({ request }) => {
    const { email, password } = await request.json()
    
    if (email === 'test@example.com' && password === 'password123') {
      return HttpResponse.json({
        token: 'mock-jwt-token-here',
        name: '테스트 사용자',
        email: 'test@example.com'
      })
    }
    
    return HttpResponse.json(
      { message: '이메일 또는 비밀번호가 일치하지 않습니다.' },
      { status: 401 }
    )
  }),

  // 회원가입
  http.post('http://localhost:8080/auth/signup', async ({ request }) => {
    const data = await request.json()
    
    return HttpResponse.json({
      token: 'mock-jwt-token-here',
      name: data.name,
      email: data.email
    }, { status: 201 })
  }),

  // 기관 목록
  http.get('http://localhost:8080/api/agencies', () => {
    return HttpResponse.json({
      content: [
        {
          id: 1,
          instiNm: "한국산업인력공단",
          address: "서울시 강남구...",
          tel: "02-1234-5678"
        }
      ],
      totalElements: 1,
      totalPages: 1,
      number: 0,
      size: 20
    })
  }),

  // Q-Net 자격 종목 목록
  http.get('http://localhost:8080/api/qnet/qualifications', () => {
    return HttpResponse.json([
      {
        jmCd: "1320",
        jmfldnm: "정보처리기사",
        seriesCd: "01",
        seriesNm: "기사"
      }
    ])
  })
]
```

### 2-4. MSW 브라우저 설정

`src/mocks/browser.js` 파일 생성:

```javascript
import { setupWorker } from 'msw/browser'
import { handlers } from './handlers'

export const worker = setupWorker(...handlers)
```

### 2-5. MSW 활성화

**React의 경우** (`src/index.js` 또는 `src/main.jsx`):

```javascript
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'

// MSW 임포트
import { worker } from './mocks/browser'

// 개발 환경에서만 MSW 활성화
if (process.env.NODE_ENV === 'development') {
  worker.start({
    onUnhandledRequest: 'bypass', // 처리되지 않은 요청은 실제 서버로
  })
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
```

**Vue의 경우** (`src/main.js`):

```javascript
import { createApp } from 'vue'
import App from './App.vue'

// MSW 임포트
import { worker } from './mocks/browser'

// 개발 환경에서만 MSW 활성화
if (process.env.NODE_ENV === 'development') {
  worker.start({
    onUnhandledRequest: 'bypass',
  }).then(() => {
    createApp(App).mount('#app')
  })
} else {
  createApp(App).mount('#app')
}
```

### 2-6. 사용 방법

MSW를 설정하면 일반적인 API 호출 코드를 그대로 사용할 수 있습니다:

```javascript
// 일반적인 fetch 사용
const response = await fetch('http://localhost:8080/api/licenses')
const data = await response.json()
console.log(data) // Mock 데이터 반환
```

브라우저 개발자 도구 콘솔에서 MSW 활성화 메시지를 확인할 수 있습니다.

---

## 방법 3: JSON Server

가장 간단하게 REST API를 모킹할 수 있는 방법입니다.

### 3-1. JSON Server 설치

```bash
npm install -g json-server
# 또는
yarn global add json-server
```

### 3-2. Mock 데이터 파일 생성

프로젝트 루트에 `db.json` 파일 생성:

```json
{
  "licenses": [
    {
      "jmcd": "1320",
      "jmfldnm": "정보처리기사",
      "seriescd": "01",
      "seriesnm": "기사",
      "qualgbcd": "T",
      "qualgbnm": "국가기술자격",
      "summary": "컴퓨터 정보처리 기술에 관한 자격",
      "job": "소프트웨어 개발, 데이터베이스 관리 등",
      "career": "IT 분야 취업 및 진로",
      "trend": "알고리즘, 데이터베이스, 네트워크 등 출제",
      "createdAt": "2024-01-01T00:00:00"
    },
    {
      "jmcd": "2290",
      "jmfldnm": "정보보안기사",
      "seriescd": "01",
      "seriesnm": "기사",
      "qualgbcd": "T",
      "qualgbnm": "국가기술자격",
      "summary": "정보보안에 관한 자격",
      "job": "보안 시스템 구축 및 운영",
      "career": "정보보안 전문가",
      "trend": "암호학, 네트워크 보안 등",
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "agencies": [
    {
      "id": 1,
      "instiNm": "한국산업인력공단",
      "address": "서울시 강남구...",
      "tel": "02-1234-5678"
    }
  ],
  "reviews": [
    {
      "reviewId": 1,
      "jmcd": "1320",
      "difficulty": "MEDIUM",
      "comment": "실기 시험이 어려웠습니다. 충분한 준비가 필요해요.",
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "users": [
    {
      "id": 1,
      "email": "test@example.com",
      "name": "테스트 사용자",
      "password": "password123"
    }
  ]
}
```

### 3-3. JSON Server 실행

```bash
json-server --watch db.json --port 8080
```

### 3-4. API 호출

```javascript
// 자격증 목록 조회
fetch('http://localhost:8080/licenses')
  .then(res => res.json())
  .then(data => console.log(data))

// 특정 자격증 조회
fetch('http://localhost:8080/licenses/1320')
  .then(res => res.json())
  .then(data => console.log(data))

// 검색 (쿼리 파라미터 사용)
fetch('http://localhost:8080/licenses?jmfldnm_like=정보')
  .then(res => res.json())
  .then(data => console.log(data))

// 페이지네이션
fetch('http://localhost:8080/licenses?_page=1&_limit=10')
  .then(res => res.json())
  .then(data => console.log(data))
```

### 3-5. package.json에 스크립트 추가

```json
{
  "scripts": {
    "mock-api": "json-server --watch db.json --port 8080"
  }
}
```

이제 `npm run mock-api`로 실행할 수 있습니다.

---

## API 문서 확인 방법

### Swagger UI 사용 (Docker 방법 사용 시)

1. 백엔드 서버 실행 후 브라우저에서 접속:
   ```
   http://localhost:8080/swagger-ui.html
   ```

2. 각 API 엔드포인트를 클릭하면 상세 정보를 확인할 수 있습니다:
   - 요청 파라미터
   - 요청 바디 형식
   - 응답 데이터 구조
   - 예시 값

3. "Try it out" 버튼을 클릭하여 실제 API를 테스트할 수 있습니다.

---

## 주요 API 엔드포인트

### 인증 (Auth)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| POST | `/auth/login` | 로그인 | ❌ |
| POST | `/auth/signup` | 회원가입 | ❌ |

**로그인 요청 예시:**
```json
{
  "email": "test@example.com",
  "password": "password123"
}
```

**로그인 응답 예시:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "name": "홍길동",
  "email": "test@example.com"
}
```

### 자격증 (Licenses)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| GET | `/api/licenses` | 전체 자격증 목록 조회 | ❌ |
| GET | `/api/licenses/search?keyword={검색어}` | 자격증 검색 | ❌ |

**응답 예시:**
```json
[
  {
    "jmcd": "1320",
    "jmfldnm": "정보처리기사",
    "seriescd": "01",
    "seriesnm": "기사",
    "qualgbcd": "T",
    "qualgbnm": "국가기술자격",
    "summary": "컴퓨터 정보처리 기술에 관한 자격",
    "job": "소프트웨어 개발, 데이터베이스 관리 등",
    "career": "IT 분야 취업 및 진로",
    "trend": "알고리즘, 데이터베이스, 네트워크 등 출제",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### 통합 검색 (Search)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| GET | `/api/search?query={검색어}` | 자격증, 후기 등 통합 검색 | ❌ |

**응답 예시:**
```json
[
  {
    "type": "LICENSE",
    "id": "1320",
    "title": "정보처리기사",
    "content": "기사",
    "createdAt": "2024-01-01T00:00:00"
  },
  {
    "type": "REVIEW",
    "id": "1",
    "title": "Re: 정보처리기사",
    "content": "실기 시험이 어려웠습니다...",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

### 기관 (Agencies)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| GET | `/api/agencies?page=0&size=20` | 기관 목록 조회 (페이지네이션) | ❌ |

**응답 예시:**
```json
{
  "content": [
    {
      "id": 1,
      "instiNm": "한국산업인력공단",
      "address": "서울시 강남구...",
      "tel": "02-1234-5678"
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "number": 0,
  "size": 20
}
```

### Q-Net 데이터 (QNet Open API)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| GET | `/api/qnet/qualifications?page=1&size=10` | 자격 종목 목록 조회 | ❌ |
| GET | `/api/qnet/qualitative-info?seriesCd={계열코드}` | 종목 상세 정보 조회 | ❌ |
| GET | `/api/qnet/exam-subjects?jmCd={종목코드}` | 시험 과목 정보 조회 | ❌ |

### 즐겨찾기 (Favorites)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| POST | `/api/favorites/licenses/{jmcd}` | 자격증 즐겨찾기 추가 | ✅ |
| DELETE | `/api/favorites/licenses/{jmcd}` | 자격증 즐겨찾기 삭제 | ✅ |
| GET | `/api/favorites/licenses` | 내 즐겨찾기 목록 조회 | ✅ |

**인증이 필요한 API 호출 방법:**
```javascript
// 로그인 후 받은 토큰 사용
const token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'

fetch('http://localhost:8080/api/favorites/licenses/1320', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
```

---

## 문제 해결 (Troubleshooting)

### Docker 관련

**문제: "Cannot connect to the Docker daemon"**
- **해결**: Docker Desktop이 실행 중인지 확인

**문제: 포트 충돌 (Port already in use)**
- **해결**: `docker-compose.yml`에서 포트 번호 변경 (예: 8080 → 8081)

**문제: 빌드가 너무 느림**
- **해결**: Docker Desktop 설정에서 리소스 할당 증가

### MSW 관련

**문제: Mock이 작동하지 않음**
- **해결**: 
  1. 브라우저 콘솔에서 MSW 활성화 메시지 확인
  2. `public/mockServiceWorker.js` 파일이 존재하는지 확인
  3. 브라우저 캐시 삭제 후 재시작

**문제: CORS 에러 발생**
- **해결**: MSW는 브라우저 레벨에서 작동하므로 CORS 에러가 발생하지 않아야 합니다. 네트워크 탭에서 요청이 실제로 가로채지는지 확인하세요.

### JSON Server 관련

**문제: JSON Server가 시작되지 않음**
- **해결**: 
  ```bash
  npm install -g json-server --force
  ```

**문제: 복잡한 검색이 안 됨**
- **해결**: JSON Server는 간단한 검색만 지원합니다. 복잡한 검색은 MSW 사용을 권장합니다.

---

## VS Code 추천 확장 프로그램

프론트엔드 개발에 도움이 되는 VS Code 확장 프로그램:

1. **REST Client** - VS Code에서 직접 API 테스트
2. **Thunder Client** - Postman 대체 도구
3. **JSON Viewer** - JSON 데이터 보기 편하게
4. **Docker** - Docker 컨테이너 관리

---

## 추가 지원

### 백엔드 팀에 문의가 필요한 경우

- API 응답 형식이 예상과 다를 때
- 새로운 API 엔드포인트가 필요할 때
- 인증 관련 문제가 발생할 때
- 데이터베이스 초기 데이터가 필요할 때

### 유용한 링크

- **Swagger UI**: http://localhost:8080/swagger-ui.html (Docker 실행 시)
- **MSW 공식 문서**: https://mswjs.io/
- **JSON Server 공식 문서**: https://github.com/typicode/json-server
- **Docker 공식 문서**: https://docs.docker.com/

---

## 정리

1. **완전한 백엔드 환경이 필요하다면**: Docker 사용 (추천)
2. **빠른 개발과 프로토타이핑이 필요하다면**: MSW 사용
3. **가장 간단한 시작이 필요하다면**: JSON Server 사용

세 가지 방법을 조합해서 사용할 수도 있습니다. 예를 들어:
- 평상시: MSW로 빠르게 개발
- 통합 테스트: Docker로 실제 백엔드와 연동

프론트엔드 팀의 생산성 향상을 기원합니다! 🚀

