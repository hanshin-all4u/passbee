package com.passbee.notice.service;

import com.passbee.common.exception.AuthorizationException;
import com.passbee.common.exception.ResourceNotFoundException;
import com.passbee.notice.domain.Notice;
import com.passbee.notice.dto.NoticeRequestDto;
import com.passbee.notice.dto.NoticeResponseDto;
import com.passbee.notice.repo.NoticeRepository;
import com.passbee.user.Role;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // ▼▼▼ [추가] StringUtils import

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UsersRepository usersRepository;

    // ... (createNotice, getNoticeDetails, updateNotice, deleteNotice, findAdminByEmail 메서드는 그대로 유지) ...

    /**
     * 공지사항 생성 (관리자만 가능)
     */
    @Transactional
    public NoticeResponseDto createNotice(String adminEmail, NoticeRequestDto requestDto) {
        Users admin = findAdminByEmail(adminEmail); // 관리자 확인

        Notice notice = Notice.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .author(admin)
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        return NoticeResponseDto.from(savedNotice);
    }

    /**
     * 공지사항 목록 조회 (모든 사용자 가능, REQ-CMM-001)
     * ▼▼▼ [수정] keyword 파라미터 추가 및 로직 분기 ▼▼▼
     */
    public Page<NoticeResponseDto> getNoticeList(String keyword, Pageable pageable) {
        Page<Notice> noticePage;
        if (StringUtils.hasText(keyword)) {
            // 키워드가 있으면 제목 또는 내용에서 검색
            noticePage = noticeRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword, keyword, pageable);
        } else {
            // 키워드가 없으면 전체 목록 조회
            noticePage = noticeRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return noticePage.map(NoticeResponseDto::from);
    }

    /**
     * 공지사항 상세 조회 (모든 사용자 가능, REQ-CMM-002)
     */
    public NoticeResponseDto getNoticeDetails(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다: " + noticeId));
        return NoticeResponseDto.from(notice);
    }

    /**
     * 공지사항 수정 (관리자만 가능)
     */
    @Transactional
    public NoticeResponseDto updateNotice(String adminEmail, Long noticeId, NoticeRequestDto requestDto) {
        findAdminByEmail(adminEmail); // 관리자 확인

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다: " + noticeId));

        notice.setTitle(requestDto.title());
        notice.setContent(requestDto.content());

        Notice updatedNotice = noticeRepository.save(notice);
        return NoticeResponseDto.from(updatedNotice);
    }

    /**
     * 공지사항 삭제 (관리자만 가능)
     */
    @Transactional
    public void deleteNotice(String adminEmail, Long noticeId) {
        findAdminByEmail(adminEmail); // 관리자 확인

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다: " + noticeId));

        noticeRepository.delete(notice);
    }

    /**
     * 이메일로 관리자(ADMIN) 사용자를 찾아 반환하는 헬퍼 메서드
     */
    private Users findAdminByEmail(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + email));
        if (user.getRole() != Role.ADMIN) {
            throw new AuthorizationException("관리자 권한이 필요합니다.");
        }
        return user;
    }
}