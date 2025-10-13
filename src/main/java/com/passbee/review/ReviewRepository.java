package com.passbee.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 특정 자격증(License)에 달린 모든 리뷰를 찾기 위한 메소드
    List<Review> findByLicenseJmcd(String jmcd);
}