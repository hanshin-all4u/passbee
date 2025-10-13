# 진행 상태 저장 기능 가이드

## 개요
데이터 수집 중 애플리케이션이 중단되어도 중단된 곳부터 다시 시작할 수 있는 진행 상태 저장 기능이 추가되었습니다.

## 주요 특징

### 1. **자동 체크포인트 저장** ✅
- 각 수집 작업의 진행 상태가 데이터베이스에 자동으로 저장됩니다
- 페이지 단위, 인덱스 단위로 진행 상태 추적
- 주기적으로 진행 상태 업데이트 (10~50개 항목마다)

### 2. **중단된 곳부터 재개** 🔄
- 애플리케이션 재시작 시 자동으로 진행 상태 확인
- 이전에 중단된 작업이 있으면 해당 위치부터 이어서 수집
- 로그에 "🔄 Resuming from..." 메시지 표시

### 3. **상태 관리**
- **NOT_STARTED**: 아직 시작하지 않음
- **IN_PROGRESS**: 진행 중 (재개 가능)
- **COMPLETED**: 완료
- **FAILED**: 실패 (에러 메시지 저장)

## 시나리오별 동작

### 시나리오 1: 정상 종료 (빨간 네모 버튼 클릭)
```
1. 사용자가 Stop 버튼 클릭
2. 현재까지 처리된 데이터는 DB에 저장됨
3. 진행 상태도 DB에 저장됨 (IN_PROGRESS 상태로)
4. 애플리케이션 종료

재시작 시:
1. 애플리케이션 시작
2. 진행 상태 확인: IN_PROGRESS 발견
3. 🔄 저장된 위치부터 자동으로 재개
4. 로그: "🔄 Resuming from page 5 (already processed 800 items)"
```

### 시나리오 2: 절전모드
```
절전모드 진입:
- 애플리케이션 일시 정지
- 마지막 체크포인트는 DB에 저장되어 있음

절전모드 해제:
- 대부분의 경우 자동으로 재개됨
- 일부 네트워크 연결이 끊어질 수 있음
- 필요시 애플리케이션 재시작하면 중단된 곳부터 재개
```

### 시나리오 3: 강제 종료 또는 크래시
```
1. 예상치 못한 종료 발생
2. 마지막 체크포인트까지의 데이터는 DB에 저장됨

재시작 시:
1. 진행 상태 확인
2. 마지막 체크포인트부터 자동 재개
3. 중복 데이터 방지 (업서트 방식)
```

## 진행 상태 확인 방법

### 1. 로그로 확인
```
// 시작 시
[QUALIFICATIONS] Collection started

// 진행 중 (재개 시)
🔄 Resuming from page 5 (already processed 800 items)

// 완료 시
[QUALIFICATIONS] Collection completed. Total items: 1500
```

### 2. 데이터베이스에서 확인
```sql
-- 진행 상태 테이블 조회
SELECT * FROM data_collection_progress;

-- 진행 중인 작업 확인
SELECT collection_type, status, current_page, processed_items, 
       last_started_at
FROM data_collection_progress
WHERE status = 'IN_PROGRESS';
```

### 3. Swagger UI에서 확인
현재는 진행 상태 조회 API가 없지만, 필요시 추가할 수 있습니다.

## 수집 작업별 재개 전략

### 1. 페이지 기반 수집 (License, Qualification, Agency)
- **저장**: 페이지 번호 (`currentPage`)
- **재개**: 마지막 처리한 페이지 다음부터 시작
- **예**: 5페이지까지 처리 → 6페이지부터 재개

### 2. 인덱스 기반 수집 (LicenseDetails, ExamSubjects)
- **저장**: 리스트 인덱스 (`currentIndex`)
- **재개**: 마지막 처리한 인덱스 다음부터 시작
- **예**: 100번째 항목까지 처리 → 101번째부터 재개

### 3. 복합 수집 (PracticalItems, Statistics, ScoreDistributions)
- **저장**: 시작/완료 상태만 저장
- **재개**: 전체 재수집 (중복 방지 로직 있음)
- **이유**: 복잡한 중첩 루프 구조로 인해 정확한 위치 추적 어려움

## 주의사항

### 1. 데이터 중복 방지
모든 수집 작업은 **업서트(Upsert)** 방식으로 동작합니다:
- 이미 존재하는 데이터는 업데이트
- 없는 데이터만 새로 삽입
- 따라서 처음부터 다시 수집해도 안전함

### 2. 체크포인트 간격
- **페이지 수집**: 매 페이지마다 저장
- **인덱스 수집**: 10개 항목마다 저장
- **실기 지참물**: 50개 항목마다 저장

### 3. 수동 초기화
진행 상태를 수동으로 초기화하려면:

```java
// DataCollectionProgressService 사용
progressService.reset("QUALIFICATIONS");  // 특정 작업 초기화
progressService.resetAll();               // 전체 초기화
```

또는 SQL:
```sql
-- 특정 작업 초기화
UPDATE data_collection_progress 
SET status = 'NOT_STARTED', 
    current_page = NULL, 
    processed_items = 0
WHERE collection_type = 'QUALIFICATIONS';

-- 전체 초기화
DELETE FROM data_collection_progress;
```

## 성능 영향

### 1. 저장 오버헤드
- 체크포인트 저장은 매우 빠름 (단일 UPDATE 쿼리)
- 전체 수집 시간에 미치는 영향: **< 1%**

### 2. 메모리 사용
- 진행 상태 정보는 메모리에 캐시되지 않음
- 매번 DB에서 읽고 쓰기
- 메모리 오버헤드: **거의 없음**

## 데이터베이스 스키마

```sql
CREATE TABLE data_collection_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collection_type VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    current_page INT,
    current_index INT,
    current_year INT,
    current_grade_code VARCHAR(10),
    current_jmcd VARCHAR(10),
    current_impl_yy VARCHAR(4),
    current_impl_seq INT,
    total_items INT,
    processed_items INT,
    last_started_at DATETIME,
    last_completed_at DATETIME,
    error_message TEXT,
    metadata TEXT,
    created_at DATETIME,
    updated_at DATETIME
);
```

## 수집 작업 타입

| 타입 | 설명 | 재개 방식 |
|------|------|-----------|
| QUALIFICATIONS | 자격증 목록 (License) | 페이지 단위 |
| QUALIFICATIONS_TO_QUALIFICATION | 자격증 목록 (Qualification) | 페이지 단위 |
| LICENSE_DETAILS | 자격증 상세 정보 | 인덱스 단위 |
| EXAM_SUBJECTS | 시험 과목 | 인덱스 단위 |
| PRACTICAL_ITEMS | 실기 지참물 | 전체 재수집 |
| AGENCIES | 기관 정보 | 페이지 단위 |
| STATISTICS | 통계 정보 | 전체 재수집 |
| SCORE_DISTRIBUTIONS | 점수 분포 | 전체 재수집 |

## FAQ

### Q1: 절전모드에서 데이터가 손실되나요?
**A**: 아니요. 마지막 체크포인트까지의 데이터는 모두 DB에 저장됩니다. 재시작 시 자동으로 이어서 수집됩니다.

### Q2: 중단 후 다시 시작하면 중복 데이터가 생기나요?
**A**: 아니요. 모든 수집 작업은 업서트 방식으로 동작하여 중복을 방지합니다.

### Q3: 진행 상태를 초기화하고 처음부터 다시 수집하려면?
**A**: `data_collection_progress` 테이블에서 해당 작업의 상태를 `NOT_STARTED`로 변경하거나 삭제하면 됩니다.

### Q4: 특정 작업만 재수집할 수 있나요?
**A**: 네. Swagger UI에서 개별 수집 API를 호출하면 됩니다. 예: `/api/admin/collect/qualifications`

### Q5: 진행률을 확인할 수 있나요?
**A**: 데이터베이스에서 `processed_items`와 `total_items`를 비교하거나, 로그를 확인하세요. 필요시 진행률 조회 API를 추가할 수 있습니다.

## 관련 파일

- `DataCollectionProgress.java` - 진행 상태 엔티티
- `DataCollectionProgressRepository.java` - 리포지토리
- `DataCollectionProgressService.java` - 진행 상태 관리 서비스
- `ScheduledDataCollector.java` - 수집 로직 (진행 상태 저장/복원 포함)

## 문의

진행 상태 저장 기능 관련 문제가 발생하면:
1. 로그에서 "🔄 Resuming" 메시지 확인
2. `data_collection_progress` 테이블 확인
3. 필요시 수동으로 상태 초기화 후 재시도

