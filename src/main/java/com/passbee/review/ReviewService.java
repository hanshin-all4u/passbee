package com.passbee.review;

import com.passbee.license.License;
import com.passbee.license.LicenseRepository;
// ↓↓↓ User를 Users로 수정했습니다. ↓↓↓
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LicenseRepository licenseRepository;
    private final UsersRepository userRepository;

    // ... (findReviewsByLicense 메소드는 그대로) ...
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> findReviewsByLicense(String jmcd) {
        List<Review> reviews = reviewRepository.findByLicenseJmcd(jmcd);
        return reviews.stream()
                .map(ReviewResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDto createReview(String jmcd, ReviewRequestDto requestDto, Long userId) {
        License license = licenseRepository.findById(jmcd)
                .orElseThrow(() -> new IllegalArgumentException("해당 자격증을 찾을 수 없습니다. id=" + jmcd));

        // ↓↓↓ 여기도 User를 Users로 수정했습니다. ↓↓↓
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. id=" + userId));

        Review review = Review.builder()
                .comment(requestDto.getComment())
                .rating(requestDto.getRating())
                .difficulty(requestDto.getDifficulty())
                .license(license)
                .user(user)
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewResponseDto.fromEntity(savedReview);
    }
}
