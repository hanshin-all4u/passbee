package com.passbee.search;

import com.passbee.license.License;
// ▼▼▼ [확인] Notice 클래스의 import 경로가 정확한지 확인하세요 ▼▼▼
import com.passbee.notice.domain.Notice;
import com.passbee.review.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IntegratedSearchResultDto {

    private final String type; // "LICENSE" | "REVIEW" | "NOTICE" 등
    private final String id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;

    // ... (fromLicense, fromReview 메서드는 그대로 유지) ...
    public static IntegratedSearchResultDto fromLicense(License license) {
        return IntegratedSearchResultDto.builder()
                .type("LICENSE")
                .id(license.getJmcd())
                .title(license.getJmfldnm())
                .content(license.getSeriesnm())
                .createdAt(license.getCreatedAt())
                .build();
    }

    public static IntegratedSearchResultDto fromReview(Review review) {
        String title = "Re: " + review.getLicense().getJmfldnm();
        String content = review.getComment();
        if (content != null && content.length() > 100) { // null 체크 추가
            content = content.substring(0, 100) + "...";
        }
        return IntegratedSearchResultDto.builder()
                .type("REVIEW")
                .id(String.valueOf(review.getReviewId()))
                .title(title)
                .content(content)
                .createdAt(review.getCreatedAt())
                .build();
    }

    // ▼▼▼ [확인] fromNotice 메서드의 내용이 아래와 같은지 확인하세요 ▼▼▼
    public static IntegratedSearchResultDto fromNotice(Notice notice) {
        String content = notice.getContent(); // Lombok @Getter가 생성한 메서드 사용
        if (content != null && content.length() > 100) { // null 체크 추가
            content = content.substring(0, 100) + "...";
        }
        return IntegratedSearchResultDto.builder()
                .type("NOTICE")
                .id(String.valueOf(notice.getId())) // Lombok @Getter가 생성한 메서드 사용
                .title(notice.getTitle())           // Lombok @Getter가 생성한 메서드 사용
                .content(content)
                .createdAt(notice.getCreatedAt()) // BaseTimeEntity의 @Getter가 생성한 메서드 사용
                .build();
    }
}