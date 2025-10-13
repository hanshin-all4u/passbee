# 자동 데이터 수집 시스템 가이드

## 개요
이 프로젝트는 Q-Net Open API로부터 자격증 관련 데이터를 자동으로 수집하여 데이터베이스에 저장하는 시스템입니다.

## 주요 변경 사항

### 1. 자동 데이터 수집 스케줄러 구현
- **파일**: `src/main/java/com/passbee/scheduler/ScheduledDataCollector.java`
- **기능**:
  - ✅ 애플리케이션 시작 시 자동으로 초기 데이터 수집 시작
  - ✅ 매일 자정(00:00)에 전체 데이터 자동 업데이트
  - ✅ 매주 월요일 오전 3시에 전체 데이터 재수집 (보험용)

### 2. 수집되는 데이터 테이블

#### 자격증 정보
- **License** (license): 자격증 기본 정보 + 상세 정보
- **Qualification** (qualification): 자격증 기본 정보 (별도 테이블)
- **ExamSubject** (exam_subject): 시험 과목 정보
- **PracticalItem** (practical_item): 실기시험 지참물 정보

#### 기관 정보
- **Agency** (agency): 자격정보 인정 기관 정보

#### 통계 정보
- **TotalExamStat** (total_exam_stat): 연도별 응시/합격 통계
- **GradeStat** (grade_stat): 등급별 응시/합격률 통계
- **RegionalReception** (regional_reception): 거주지별 접수 현황
- **ScoreDistribution** (score_distribution): 점수 분포 통계

#### 사용자 생성 데이터 (자동 수집 제외)
- **Users**: 사용자 계정 (회원가입으로 생성)
- **Review**: 사용자 리뷰 (사용자가 직접 작성)
- **Favorite**: 즐겨찾기 (사용자가 직접 설정)
- **AttachmentFile**: 첨부파일 (리뷰 작성 시 업로드)

## 사용 방법

### 1. 자동 수집 (권장)
애플리케이션을 실행하면 자동으로 데이터 수집이 시작됩니다:

```bash
# IntelliJ IDEA에서 실행
All4uServerApplication 클래스의 main 메서드 실행

# 또는 Gradle로 실행
./gradlew bootRun
```

**실행 시 로그**:
```
========================================
애플리케이션 시작 완료 - 초기 데이터 수집을 시작합니다...
========================================
===== 1. Collecting qualifications... =====
===== 1-1. Collecting qualifications to Qualification table... =====
===== 2. Enriching license details... =====
===== 2-1. Collecting exam subjects... =====
===== 2-2. Collecting practical exam items... =====
===== 3. Collecting agencies... =====
===== 4. Collecting all statistics... =====
===== 5. Collecting score distributions... =====
========================================
초기 데이터 수집이 완료되었습니다.
이제 매일 자정에 자동으로 데이터가 업데이트됩니다.
========================================
```

### 2. 수동 수집 (관리자용)
Swagger UI를 통해 수동으로 데이터를 수집할 수 있습니다:

1. 브라우저에서 `http://localhost:8080/swagger-ui.html` 접속
2. **Data Collection (Admin)** 섹션 선택
3. 원하는 수집 작업 실행:
   - `POST /api/admin/collect/all` - 전체 데이터 수집
   - `POST /api/admin/collect/qualifications` - 자격증 목록만 수집
   - `POST /api/admin/collect/exam-subjects` - 시험 과목만 수집
   - `POST /api/admin/collect/practical-items` - 실기 지참물만 수집
   - `POST /api/admin/collect/agencies` - 기관 정보만 수집
   - `POST /api/admin/collect/statistics` - 통계 정보만 수집
   - `POST /api/admin/collect/score-distributions` - 점수 분포만 수집

### 3. 스케줄 수정
`ScheduledDataCollector.java` 파일에서 스케줄을 변경할 수 있습니다:

```java
// 매일 자정 실행
@Scheduled(cron = "0 0 0 * * *")
public void scheduledCollectAllData() { ... }

// 매주 월요일 오전 3시 실행
@Scheduled(cron = "0 0 3 * * 1")
public void weeklyFullRefresh() { ... }
```

**Cron 표현식 형식**: `초 분 시 일 월 요일`
- 예: `0 0 0 * * *` = 매일 자정
- 예: `0 0 3 * * 1` = 매주 월요일 오전 3시
- 예: `0 0 */6 * * *` = 6시간마다

## 데이터 수집 프로세스

### 1단계: 자격증 기본 정보 수집
- Q-Net API에서 전체 자격증 목록 조회
- License 테이블과 Qualification 테이블에 저장

### 2단계: 자격증 상세 정보 보강
- 각 자격증의 상세 정보(개요, 수행직무, 진로 등) 조회
- License 테이블에 추가 정보 업데이트

### 3단계: 시험 과목 수집
- 각 자격증의 시험 과목 정보 수집
- ExamSubject 테이블에 저장

### 4단계: 실기시험 지참물 수집
- 최근 3년간 각 자격증의 실기시험 지참물 정보 수집
- PracticalItem 테이블에 저장

### 5단계: 기관 정보 수집
- 자격정보 인정 기관 목록 수집
- Agency 테이블에 저장

### 6단계: 통계 정보 수집
- 최근 6년간 통계 데이터 수집:
  - 연도별 응시/합격 통계
  - 등급별 통계
  - 거주지별 접수 현황
- 각각의 통계 테이블에 저장

### 7단계: 점수 분포 수집
- 최근 3년간 등급별 점수 분포 수집
- ScoreDistribution 테이블에 저장

## 성능 최적화

### 1. API 호출 제한
- 각 API 호출 간 50ms 지연 (`THROTTLE_MS`)
- 서버 과부하 방지

### 2. 배치 저장
- 페이지 단위로 데이터 수집 후 일괄 저장
- 데이터베이스 트랜잭션 최적화

### 3. 캐싱
- seriescd별 상세 정보 캐싱
- 동일 시리즈 반복 호출 감소

### 4. 업서트 방식
- 기존 데이터가 있으면 업데이트
- 없으면 새로 생성
- 중복 데이터 방지

## 트러블슈팅

### 1. 데이터 수집이 시작되지 않는 경우
- 애플리케이션 로그 확인: `Initial data collection started`
- Q-Net API 연결 상태 확인
- 데이터베이스 연결 상태 확인

### 2. 특정 테이블에 데이터가 없는 경우
- 해당 자격증/연도에 데이터가 없을 수 있음
- 로그에서 `No data found` 메시지 확인
- Swagger UI에서 해당 수집 작업만 다시 실행

### 3. API 호출 실패
- Q-Net API 서버 상태 확인
- 네트워크 연결 상태 확인
- API 키 설정 확인 (`application.yml`)

## 데이터 조회 API

수집된 데이터는 다음 API로 조회할 수 있습니다:

- `GET /api/licenses` - 자격증 목록
- `GET /api/exam-subjects` - 시험 과목 목록
- `GET /api/agencies` - 기관 목록
- `GET /api/qnet/*` - Q-Net 원본 데이터 프록시 (실시간 조회)

자세한 내용은 Swagger UI를 참고하세요.

## 주의사항

1. **초기 데이터 수집 시간**: 전체 데이터 수집에는 1-2시간 정도 소요될 수 있습니다.
2. **API 호출 제한**: Q-Net API 사용량 제한에 주의하세요.
3. **데이터베이스 용량**: 충분한 저장 공간을 확보하세요.
4. **백그라운드 실행**: 초기 데이터 수집은 별도 스레드에서 실행되므로 애플리케이션 시작이 차단되지 않습니다.

## 관련 파일

- `ScheduledDataCollector.java` - 데이터 수집 메인 로직
- `DataCollectionController.java` - 수동 수집 API 컨트롤러
- `QnetDataService.java` - Q-Net API 호출 서비스
- `All4uServerApplication.java` - 스케줄링 활성화 설정

## 문의

데이터 수집 관련 문제가 발생하면 로그를 확인하거나 관리자에게 문의하세요.

