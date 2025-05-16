# 블로그 서비스

Scala 3.7.0과 http4s, Doobie, Circe를 사용하여 구현된 웹 블로그 서비스입니다.

## 주요 기능

- 블로그 게시물 관리를 위한 RESTful API
- 데이터 저장을 위한 인메모리 H2 데이터베이스
- 간단하고 반응형 웹 인터페이스
- 블로그 게시물에 대한 CRUD 작업
- 태그 기능 지원 (세미콜론으로 구분된 문자열로 저장)
- 제목, 내용, 작성자 또는 태그로 게시물 검색 기능

## 사용 기술

- Scala 3.7.0 - 함수형 프로그래밍 언어
- http4s - 타입 안전한 HTTP 서버 및 클라이언트 라이브러리
- Doobie - 함수형 JDBC 레이어
- Circe - JSON 인코딩/디코딩 라이브러리
- H2 Database - 인메모리 관계형 데이터베이스
- Cats Effect - 함수형 이펙트 시스템

## 프로젝트 구조

```
src/main/scala/com/example/blog/
├── api/                # HTTP API 엔드포인트
│   ├── BlogApi.scala         # 블로그 API 구현
│   └── StaticFileService.scala # 정적 파일 서비스
├── config/             # 애플리케이션 설정
│   └── AppConfig.scala       # 설정 클래스
├── model/              # 데이터 모델
│   └── Post.scala            # 게시물 모델
├── repository/         # 데이터베이스 접근 계층
│   └── PostRepository.scala  # 게시물 저장소
├── service/            # 비즈니스 로직 계층
│   └── PostService.scala     # 게시물 서비스
└── BlogServer.scala    # 메인 서버 애플리케이션

src/main/resources/
└── static/             # 정적 리소스
    └── index.html            # 메인 웹 인터페이스
```

## 시작하기

### 필수 조건

- JDK 11 이상
- sbt 1.5.0 이상

### 애플리케이션 실행

```bash
sbt run
```

서버는 http://localhost:8080 에서 시작됩니다.

### API 엔드포인트

- `GET /api/posts` - 모든 게시물 가져오기
- `GET /api/posts/{id}` - ID로 특정 게시물 가져오기
- `POST /api/posts` - 새 게시물 생성
- `PUT /api/posts/{id}` - 기존 게시물 업데이트
- `DELETE /api/posts/{id}` - 게시물 삭제
- `GET /api/posts/search/{searchTerm}` - 게시물 검색

### 게시물 JSON 형식

```json
{
  "title": "게시물 제목",
  "content": "게시물 내용",
  "author": "작성자 이름",
  "tags": "태그1;태그2;태그3"
}
```

### 웹 인터페이스

간단한 웹 인터페이스는 http://localhost:8080 에서 사용할 수 있습니다.

## 개발

### 프로젝트 빌드

```bash
sbt compile
```

### 테스트 실행

```bash
sbt test
```

### 디버깅

로그는 콘솔에 출력되며, 서버 로그에서 요청 및 응답 정보를 확인할 수 있습니다.

### 코드 포맷팅

프로젝트의 코드 스타일을 일관되게 유지하기 위해 Scalafmt를 사용할 수 있습니다:

```bash
# 모든 파일 포맷팅
sbt scalafmtAll

# 변경된 파일만 포맷팅
sbt scalafmt

# 테스트 코드 포맷팅
sbt scalafmtTest

# 빌드 정의 파일(build.sbt) 포맷팅
sbt scalafmtSbt
```

## 기술적 특징

- 클린 아키텍처 패턴 적용
- 함수형 프로그래밍 원칙 준수
- 타입 안전성 보장
- JSON 직렬화/역직렬화를 위한 커스텀 인코더/디코더
- 비동기 및 논블로킹 IO

## 라이선스

이 프로젝트는 오픈 소스이며 MIT 라이선스에 따라 사용할 수 있습니다.
