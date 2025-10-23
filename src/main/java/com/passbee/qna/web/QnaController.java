package com.passbee.qna.web;

import com.passbee.qna.dto.QnaRequestDto;
import com.passbee.qna.dto.QnaResponseDto;
import com.passbee.qna.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // ▼▼▼ [추가] import 구문
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Q&A", description = "Q&A 게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaController {

    private final QnaService qnaService;

    // 1. Q&A 질문 작성
    @Operation(summary = "Q&A 질문 작성")
    @PostMapping
    public ResponseEntity<?> createQna(
            @Valid @RequestBody QnaRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        QnaResponseDto createdQna = qnaService.createQna(userDetails.getUsername(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQna);
    }

    // 2. Q&A 목록 조회 (페이징, 최신순)
    @Operation(summary = "Q&A 목록 조회 (페이징, 최신순)")
    @GetMapping
    public ResponseEntity<Page<QnaResponseDto>> getQnaList(
            // ▼▼▼ [수정] sort와 direction을 분리합니다.
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {

        Page<QnaResponseDto> qnaPage = qnaService.getQnaList(userDetails, pageable);
        return ResponseEntity.ok(qnaPage);
    }

    // 3. Q&A 상세 조회
    @Operation(summary = "Q&A 상세 조회")
    @GetMapping("/{qnaId}")
    public ResponseEntity<QnaResponseDto> getQnaDetails(
            @PathVariable Long qnaId,
            @AuthenticationPrincipal UserDetails userDetails) {

        QnaResponseDto qna = qnaService.getQnaDetails(qnaId, userDetails);
        return ResponseEntity.ok(qna);
    }

    // 4. Q&A 수정 (본인만 가능)
    @Operation(summary = "Q&A 수정 (본인만 가능)")
    @PatchMapping("/{qnaId}")
    public ResponseEntity<?> updateQna(
            @PathVariable Long qnaId,
            @Valid @RequestBody QnaRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        QnaResponseDto updatedQna = qnaService.updateQna(userDetails.getUsername(), qnaId, requestDto);
        return ResponseEntity.ok(updatedQna);
    }

    // 5. Q&A 삭제 (본인 또는 관리자만 가능)
    @Operation(summary = "Q&A 삭제 (본인 또는 관리자만 가능)")
    @DeleteMapping("/{qnaId}")
    public ResponseEntity<?> deleteQna(
            @PathVariable Long qnaId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        qnaService.deleteQna(userDetails.getUsername(), qnaId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}