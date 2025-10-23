package com.passbee.comment.service;

import com.passbee.common.exception.AuthorizationException;
import com.passbee.common.exception.ResourceNotFoundException;
import com.passbee.comment.domain.Comment;
import com.passbee.comment.dto.CommentRequestDto;
import com.passbee.comment.dto.CommentResponseDto;
import com.passbee.comment.repo.CommentRepository;
import com.passbee.review.Review;
import com.passbee.review.ReviewRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final UsersRepository usersRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 특정 후기에 댓글 생성
     */
    @Transactional
    public CommentResponseDto createComment(String userEmail, Long reviewId, CommentRequestDto requestDto) {
        // 1. 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 2. 원본 후기글 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("원본 후기글을 찾을 수 없습니다: " + reviewId));

        // 3. 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .content(requestDto.content())
                .user(user)
                .review(review)
                .build();

        // 4. 저장
        Comment savedComment = commentRepository.save(comment);

        return CommentResponseDto.from(savedComment);
    }

    /**
     * 특정 후기의 모든 댓글 조회
     */
    public List<CommentResponseDto> getCommentsByReview(Long reviewId) {
        // 1. 원본 후기글 존재 여부 확인
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("원본 후기글을 찾을 수 없습니다: " + reviewId);
        }

        // 2. 댓글 목록 조회
        List<Comment> comments = commentRepository.findByReview_ReviewId(reviewId);

        // 3. DTO로 변환하여 반환
        return comments.stream()
                .map(CommentResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentResponseDto updateComment(String userEmail, Long commentId, CommentRequestDto requestDto) {
        // 1. 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 2. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다: " + commentId));

        // 3. 권한 확인 (댓글 작성자와 현재 로그인한 사용자가 동일한지)
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AuthorizationException("이 댓글을 수정할 권한이 없습니다.");
        }

        // 4. 내용 수정
        comment.setContent(requestDto.content());
        Comment updatedComment = commentRepository.save(comment);

        return CommentResponseDto.from(updatedComment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(String userEmail, Long commentId) {
        // 1. 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 2. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다: " + commentId));

        // 3. 권한 확인
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AuthorizationException("이 댓글을 삭제할 권한이 없습니다.");
        }

        // 4. 삭제
        commentRepository.delete(comment);
    }
}