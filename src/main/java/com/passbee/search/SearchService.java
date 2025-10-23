package com.passbee.search;

import com.passbee.license.LicenseRepository;
import com.passbee.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final LicenseRepository licenseRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<IntegratedSearchResultDto> searchAll(String query) {
        System.out.println("🔍 검색 시작 - 검색어: '" + query + "'");
        
        if (query == null || query.trim().isEmpty()) {
            System.out.println("❌ 검색어가 비어있음");
            return new ArrayList<>();
        }

        // 데이터베이스 상태 확인
        long totalLicenses = licenseRepository.count();
        long totalReviews = reviewRepository.count();
        System.out.println("📊 데이터베이스 상태 - 자격증: " + totalLicenses + "개, 후기: " + totalReviews + "개");

        // 1. 자격증 검색
        System.out.println("🔍 자격증 검색 중...");
        List<com.passbee.license.License> licenseResults = licenseRepository.findByJmfldnmContainingIgnoreCase(query);
        System.out.println("📋 자격증 검색 결과: " + licenseResults.size() + "개");
        
        Stream<IntegratedSearchResultDto> licenseStream = licenseResults.stream()
                .map(IntegratedSearchResultDto::fromLicense);

        // 2. 시험 후기 검색
        System.out.println("🔍 후기 검색 중...");
        List<com.passbee.review.Review> reviewResults = reviewRepository.findByCommentContainingIgnoreCase(query);
        System.out.println("📋 후기 검색 결과: " + reviewResults.size() + "개");
        
        Stream<IntegratedSearchResultDto> reviewStream = reviewResults.stream()
                .map(IntegratedSearchResultDto::fromReview);

        // 3. 모든 검색 결과를 하나의 리스트로 합치기
        List<IntegratedSearchResultDto> combinedResults = Stream.concat(licenseStream, reviewStream)
                .collect(Collectors.toList());

        System.out.println("📋 통합 검색 결과: " + combinedResults.size() + "개");

        // 최신순으로 정렬
        combinedResults.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        return combinedResults;
    }
}