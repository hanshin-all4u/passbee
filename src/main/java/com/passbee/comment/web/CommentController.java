package com.passbee.comment.web;

import com.passbee.comment.dto.CommentRequestDto;
import com.passbee.comment.dto.CommentResponseDto;
import com.passbee.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Comments", description = "댓글 API (후기 및 Q&A 연동)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    // 1. 특정 후기(Review)에 댓글 작성
    @Operation(summary = "특정 후기(Review)에 댓글 작성")
    @PostMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<?> createCommentOnReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        CommentResponseDto createdComment = commentService.createComment(userDetails.getUsername(), reviewId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    // 2. 특정 후기(Review)의 모든 댓글 조회
    @Operation(summary = "특정 후기(Review)의 모든 댓글 조회")
    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByReview(
            @PathVariable Long reviewId) {

        List<CommentResponseDto> comments = commentService.getCommentsByReview(reviewId);
        return ResponseEntity.ok(comments);
    }

    // 3. 댓글 수정 (본인만 가능)
    @Operation(summary = "댓글 수정 (본인만 가능)")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        CommentResponseDto updatedComment = commentService.updateComment(userDetails.getUsername(), commentId, requestDto);
        return ResponseEntity.ok(updatedComment);
    }

    // 4. 댓글 삭제 (본인만 가능)
    @Operation(summary = "댓글 삭제 (본인만 가능)")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        commentService.deleteComment(userDetails.getUsername(), commentId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}