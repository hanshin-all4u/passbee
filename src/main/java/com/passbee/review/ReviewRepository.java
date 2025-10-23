package com.passbee.review;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // List import 추가

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // ▼▼▼ [기존] 후기 내용(comment)으로 검색하는 메소드 (유지) ▼▼▼
    List<Review> findByCommentContainingIgnoreCase(String keyword);

    // ▼▼▼ [추가] 자격증 ID(jmcd)를 기준으로 모든 후기를 찾는 메서드 ▼▼▼
    // Review 엔티티는 License 객체를 'license'라는 필드로 가지고 있고,
    // License 엔티티는 'jmcd'를 ID로 가지고 있습니다.
    List<Review> findByLicense_Jmcd(String jmcd);
}