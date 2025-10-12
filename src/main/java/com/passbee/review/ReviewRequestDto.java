package com.passbee.review;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 리뷰 생성을 요청할 때 사용하는 DTO
@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private String comment;
    private int rating;
    private Review.Difficulty difficulty;
}

