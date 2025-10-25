package com.passbee.notice.web;

import com.passbee.notice.dto.NoticeRequestDto;
import com.passbee.notice.dto.NoticeResponseDto;
import com.passbee.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Notices", description = "공지사항 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    // --- 관리자 전용 API (생성, 수정, 삭제) ---
    // ... (createNotice, updateNotice, deleteNotice 메서드는 그대로 유지) ...
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "공지사항 생성 (관리자 전용)", security = @SecurityRequirement(name = "JWT TOKEN"))
    public ResponseEntity<?> createNotice(
            @Valid @RequestBody NoticeRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        NoticeResponseDto createdNotice = noticeService.createNotice(userDetails.getUsername(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotice);
    }

    @PatchMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "공지사항 수정 (관리자 전용)", security = @SecurityRequirement(name = "JWT TOKEN"))
    public ResponseEntity<?> updateNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        NoticeResponseDto updatedNotice = noticeService.updateNotice(userDetails.getUsername(), noticeId, requestDto);
        return ResponseEntity.ok(updatedNotice);
    }

    @DeleteMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "공지사항 삭제 (관리자 전용)", security = @SecurityRequirement(name = "JWT TOKEN"))
    public ResponseEntity<?> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        noticeService.deleteNotice(userDetails.getUsername(), noticeId);
        return ResponseEntity.noContent().build();
    }


    // --- 모든 사용자 접근 가능 API (목록, 상세 조회) ---

    @GetMapping
    // ▼▼▼ [수정] Operation 설명 변경 및 keyword 파라미터 추가 ▼▼▼
    @Operation(summary = "공지사항 목록 조회 (검색/페이징, 최신순)", description = "keyword 파라미터로 제목 또는 내용 검색 가능")
    public ResponseEntity<Page<NoticeResponseDto>> getNoticeList(
            @RequestParam(required = false) String keyword, // keyword 파라미터 추가
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // ▼▼▼ [수정] keyword를 서비스로 전달 ▼▼▼
        Page<NoticeResponseDto> noticePage = noticeService.getNoticeList(keyword, pageable);
        return ResponseEntity.ok(noticePage);
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "공지사항 상세 조회")
    public ResponseEntity<NoticeResponseDto> getNoticeDetails(@PathVariable Long noticeId) {
        NoticeResponseDto notice = noticeService.getNoticeDetails(noticeId);
        return ResponseEntity.ok(notice);
    }
}