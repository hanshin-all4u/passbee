package com.passbee.comment.repo;

import com.passbee.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 후기(Review)에 달린 모든 댓글을 조회
    List<Comment> findByReview_ReviewId(Long reviewId);
}