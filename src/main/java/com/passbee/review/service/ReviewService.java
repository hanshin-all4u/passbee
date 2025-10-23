package com.passbee.review.service;

import com.passbee.common.exception.AuthorizationException;
import com.passbee.common.exception.ResourceNotFoundException;
import com.passbee.license.License;
import com.passbee.license.LicenseRepository;
import com.passbee.review.Review;
import com.passbee.review.ReviewRepository;
import com.passbee.review.dto.ReviewRequestDto;
import com.passbee.review.dto.ReviewResponseDto;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UsersRepository usersRepository;
    private final LicenseRepository licenseRepository;

    /**
     * 후기 생성
     */
    @Transactional
    public ReviewResponseDto createReview(String userEmail, String jmcd, ReviewRequestDto requestDto) {
        // 1. 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 2. 자격증 조회
        License license = licenseRepository.findById(jmcd)
                .orElseThrow(() -> new ResourceNotFoundException("자격증을 찾을 수 없습니다: " + jmcd));

        // 3. 리뷰 엔티티 생성
        Review review = Review.builder()
                .user(user)
                .license(license)
                .difficulty(requestDto.difficulty())
                .comment(requestDto.comment())
                .build();

        // 4. 저장
        Review savedReview = reviewRepository.save(review);

        // 5. DTO로 변환하여 반환
        return ReviewResponseDto.from(savedReview);
    }

    /**
     * 특정 자격증의 모든 후기 조회
     */
    public List<ReviewResponseDto> getReviewsByLicense(String jmcd) {
        // 1. 자격증 존재 여부 확인 (선택 사항이지만, 404를 명확히 주기 위해)
        if (!licenseRepository.existsById(jmcd)) {
            throw new ResourceNotFoundException("자격증을 찾을 수 없습니다: " + jmcd);
        }

        // 2. 리포지토리에서 jmcd로 후기 목록 조회
        List<Review> reviews = reviewRepository.findByLicense_Jmcd(jmcd);

        // 3. DTO 리스트로 변환하여 반환
        return reviews.stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 단일 후기 상세 조회
     */
    public ReviewResponseDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("리뷰를 찾을 수 없습니다: " + reviewId));

        return ReviewResponseDto.from(review);
    }

    /**
     * 후기 수정
     */
    @Transactional
    public ReviewResponseDto updateReview(String userEmail, Long reviewId, ReviewRequestDto requestDto) {
        // 1. 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 2. 리뷰 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("리뷰를 찾을 수 없습니다: " + reviewId));

        // 3. 권한 확인 (리뷰 작성자와 현재 로그인한 사용자가 동일한지)
        if (!review.getUser().getId().equals(user.getId())) {
            throw new AuthorizationException("이 리뷰를 수정할 권한이 없습니다.");
        }

        // 4. 내용 수정 (setter 사용)
        review.setDifficulty(requestDto.difficulty());
        review.setComment(requestDto.comment());

        // 5. 저장 (Transactional에 의해 자동 dirty checking 되지만, 명시적 save도 가능)
        Review updatedReview = reviewRepository.save(review);

        return ReviewResponseDto.from(updatedReview);
    }

    /**
     * 후기 삭제
     */
    @Transactional
    public void deleteReview(String userEmail, Long reviewId) {
        // 1. 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 2. 리뷰 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("리뷰를 찾을 수 없습니다: " + reviewId));

        // 3. 권한 확인
        if (!review.getUser().getId().equals(user.getId())) {
            throw new AuthorizationException("이 리뷰를 삭제할 권한이 없습니다.");
        }

        // 4. 삭제
        reviewRepository.delete(review);
    }
}