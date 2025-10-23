package com.passbee.search;

import com.passbee.license.License;
import com.passbee.review.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IntegratedSearchResultDto {

    private final String type; // "LICENSE" | "REVIEW" | "POST" 등 결과의 종류
    private final String id;   // 결과물의 고유 ID (자격증은 jmcd, 나머지는 Long id)
    private final String title; // 결과물의 제목 (자격증 이름, 게시글 제목 등)
    private final String content; // (선택) 미리보기용 내용
    private final LocalDateTime createdAt; // 생성일

    // License 엔티티를 통합 검색 DTO로 변환
    public static IntegratedSearchResultDto fromLicense(License license) {
        return IntegratedSearchResultDto.builder()
                .type("LICENSE")
                .id(license.getJmcd())
                .title(license.getJmfldnm())
                .content(license.getSeriesnm()) // 내용은 계열 이름으로 대체
                .createdAt(license.getCreatedAt())
                .build();
    }

    // Review 엔티티를 통합 검색 DTO로 변환
    public static IntegratedSearchResultDto fromReview(Review review) {
        String title = "Re: " + review.getLicense().getJmfldnm();
        String content = review.getComment();
        if (content.length() > 100) { // 내용이 너무 길면 잘라내기
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
}