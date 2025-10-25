package com.passbee.notice.repo;

import com.passbee.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // List import 확인!

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 1. 키워드 없이 전체 목록 조회 (페이징, 최신순)
    // NoticeService의 getNoticeList에서 사용
    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 2. 제목 또는 내용에 키워드가 포함된 공지사항 검색 (페이징, 최신순)
    // NoticeService의 getNoticeList와 SearchService에서 공통으로 사용
    Page<Notice> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(String titleKeyword, String contentKeyword, Pageable pageable);

    // 3. [삭제] 모호성을 유발하던 List 반환 메서드를 삭제합니다.
    // List<Notice> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(String titleKeyword, String contentKeyword);

}