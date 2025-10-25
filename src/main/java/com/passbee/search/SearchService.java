package com.passbee.search;

// ▼▼▼ [확인] 필요한 모든 클래스를 정확한 경로로 import 합니다. ▼▼▼
import com.passbee.license.License;
import com.passbee.license.LicenseRepository;
import com.passbee.notice.domain.Notice; // Notice 클래스 import
import com.passbee.notice.repo.NoticeRepository;
import com.passbee.review.Review;
import com.passbee.review.ReviewRepository;
import com.passbee.search.IntegratedSearchResultDto; // DTO import (search 패키지 바로 아래)
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List; // java.util.List import
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final LicenseRepository licenseRepository;
    private final ReviewRepository reviewRepository;
    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public List<IntegratedSearchResultDto> searchAll(String query, Pageable pageable) {
        System.out.println("🔍 통합 검색 시작 - 검색어: '" + query + "', 페이징: " + pageable);

        if (query == null || query.trim().isEmpty()) {
            System.out.println("❌ 검색어가 비어있음");
            return new ArrayList<>();
        }

        long totalLicenses = licenseRepository.count();
        long totalReviews = reviewRepository.count();
        long totalNotices = noticeRepository.count();
        System.out.println("📊 데이터베이스 상태 - 자격증: " + totalLicenses + "개, 후기: " + totalReviews + "개, 공지: " + totalNotices + "개");


        // 1. 자격증 검색 (페이징 적용됨)
        System.out.println("🔍 자격증 검색 중...");
        Page<License> licensePage = licenseRepository.findByJmfldnmContaining(query, pageable);
        List<License> licenseResults = licensePage.getContent();
        System.out.println("📋 자격증 검색 결과 (현재 페이지): " + licenseResults.size() + "개 / 총 " + licensePage.getTotalElements() + "개");
        Stream<IntegratedSearchResultDto> licenseStream = licenseResults.stream()
                .map(IntegratedSearchResultDto::fromLicense);

        // 2. 시험 후기 검색 (페이징 미적용)
        System.out.println("🔍 후기 검색 중...");
        List<Review> reviewResults = reviewRepository.findByCommentContainingIgnoreCase(query); // Review 타입 확인
        System.out.println("📋 후기 검색 결과: " + reviewResults.size() + "개");
        Stream<IntegratedSearchResultDto> reviewStream = reviewResults.stream()
                .map(IntegratedSearchResultDto::fromReview);

        // 3. 공지사항 검색 (페이징 미적용)
        System.out.println("🔍 공지사항 검색 중...");
        // ▼▼▼ 오류 발생 예상 지점 (라인 62 근처) ▼▼▼
        List<Notice> noticeResults = noticeRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(query, query); // Notice 타입 확인
        System.out.println("📋 공지사항 검색 결과: " + noticeResults.size() + "개");
        // ▼▼▼ 오류 발생 예상 지점 (라인 65 근처) ▼▼▼
        Stream<IntegratedSearchResultDto> noticeStream = noticeResults.stream()
                .map(IntegratedSearchResultDto::fromNotice);


        // 4. 모든 검색 결과를 하나의 리스트로 합치기
        List<IntegratedSearchResultDto> combinedResults = Stream.of(licenseStream, reviewStream, noticeStream)
                .flatMap(s -> s)
                .collect(Collectors.toList());

        System.out.println("📋 통합 검색 결과 (현재 페이지 기준): " + combinedResults.size() + "개");

        // 최신순으로 정렬
        combinedResults.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        return combinedResults;
    }
}