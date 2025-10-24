package com.passbee.search;

import com.passbee.license.License;
import com.passbee.license.LicenseRepository;
import com.passbee.review.Review;
import com.passbee.review.ReviewRepository;
import com.passbee.search.IntegratedSearchResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ▼▼▼ [삭제] java.time.LocalDateTime import는 DTO 파일로 이동했으므로 삭제 가능 ▼▼▼
// import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ▼▼▼ [삭제] SearchService 내부에 정의했던 IntegratedSearchResultDto 클래스 전체 삭제 ▼▼▼
/*
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
class IntegratedSearchResultDto { ... } // 이 부분 전체 삭제
*/

@Service
@RequiredArgsConstructor
public class SearchService {

    private final LicenseRepository licenseRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<IntegratedSearchResultDto> searchAll(String query, Pageable pageable) { // Pageable 추가 확인 { // Pageable 추가 확인
        System.out.println("🔍 검색 시작 - 검색어: '" + query + "', 페이징: " + pageable);

        if (query == null || query.trim().isEmpty()) {
            System.out.println("❌ 검색어가 비어있음");
            return new ArrayList<>();
        }

        long totalLicenses = licenseRepository.count();
        long totalReviews = reviewRepository.count();
        System.out.println("📊 데이터베이스 상태 - 자격증: " + totalLicenses + "개, 후기: " + totalReviews + "개");

        // 1. 자격증 검색
        System.out.println("🔍 자격증 검색 중...");
        Page<License> licensePage = licenseRepository.findByJmfldnmContaining(query, pageable); // 수정된 메서드 호출 확인
        List<License> licenseResults = licensePage.getContent();
        System.out.println("📋 자격증 검색 결과 (현재 페이지): " + licenseResults.size() + "개 / 총 " + licensePage.getTotalElements() + "개");

        // ▼▼▼ [수정] 이제 외부 DTO 클래스의 정적 메서드를 사용합니다. ▼▼▼
        Stream<IntegratedSearchResultDto> licenseStream = licenseResults.stream()
                .map(IntegratedSearchResultDto::fromLicense);

        // 2. 시험 후기 검색
        System.out.println("🔍 후기 검색 중...");
        List<com.passbee.review.Review> reviewResults = reviewRepository.findByCommentContainingIgnoreCase(query);
        System.out.println("📋 후기 검색 결과: " + reviewResults.size() + "개");

        // ▼▼▼ [수정] 이제 외부 DTO 클래스의 정적 메서드를 사용합니다. ▼▼▼
        Stream<IntegratedSearchResultDto> reviewStream = reviewResults.stream()
                .map(IntegratedSearchResultDto::fromReview);

        // 3. 모든 검색 결과를 하나의 리스트로 합치기
        List<IntegratedSearchResultDto> combinedResults = Stream.concat(licenseStream, reviewStream)
                .collect(Collectors.toList());

        System.out.println("📋 통합 검색 결과 (현재 페이지 기준): " + combinedResults.size() + "개");

        combinedResults.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        return combinedResults;
    }
}