package com.passbee.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // List import 추가

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // ▼▼▼ [수정] SearchService에서 페이징을 사용할 수 있도록 List -> Page로 변경 ▼▼▼
    Page<Review> findByCommentContainingIgnoreCase(String keyword, Pageable pageable);

    // ▼▼▼ [유지] 자격증 ID(jmcd)를 기준으로 모든 후기를 찾는 메서드 ▼▼▼
    // Review 엔티티는 License 객체를 'license'라는 필드로 가지고 있고,
    // License 엔티티는 'jmcd'를 ID로 가지고 있습니다.
    List<Review> findByLicense_Jmcd(String jmcd);
}