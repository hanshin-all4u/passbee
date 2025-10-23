package com.passbee.qna.service;

import com.passbee.common.exception.AuthorizationException;
import com.passbee.common.exception.ResourceNotFoundException;
import com.passbee.qna.domain.Qna;
import com.passbee.qna.dto.QnaRequestDto;
import com.passbee.qna.dto.QnaResponseDto;
import com.passbee.qna.repo.QnaRepository;
import com.passbee.user.Role; // ▼▼▼ [추가] Role import
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails; // ▼▼▼ [추가]
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UsersRepository usersRepository;

    /**
     * Q&A 질문 생성
     */
    @Transactional
    public QnaResponseDto createQna(String userEmail, QnaRequestDto requestDto) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        Qna qna = Qna.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .user(user)
                .secret(requestDto.secret()) // ▼▼▼ [수정] secret 필드 저장
                .build();

        Qna savedQna = qnaRepository.save(qna);
        return QnaResponseDto.from(savedQna);
    }

    /**
     * Q&A 목록 페이징 조회 (최신순)
     * ▼▼▼ [수정] 비밀글 처리를 위해 UserDetails 추가
     */
    public Page<QnaResponseDto> getQnaList(UserDetails userDetails, Pageable pageable) {
        // 현재 로그인한 사용자 정보 (비로그인 시 null)
        Users currentUser = (userDetails != null)
                ? usersRepository.findByEmail(userDetails.getUsername()).orElse(null)
                : null;

        // ▼▼▼ [수정] qnaRepository.findAllByOrderByCreatedAtDesc(pageable) -> qnaRepository.findAll(pageable)로 변경
        Page<Qna> qnaPage = qnaRepository.findAll(pageable);

        // ▼▼▼ [수정] 각 게시글을 DTO로 변환 시 권한 확인
        return qnaPage.map(qna -> convertToDtoWithPermissionCheck(qna, currentUser));
    }

    /**
     * Q&A 상세 조회
     * ▼▼▼ [수정] 비밀글 처리를 위해 UserDetails 추가
     */
    public QnaResponseDto getQnaDetails(Long qnaId, UserDetails userDetails) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ResourceNotFoundException("Q&A 게시글을 찾을 수 없습니다: " + qnaId));

        // 현재 로그인한 사용자 정보 (비로그인 시 null)
        Users currentUser = (userDetails != null)
                ? usersRepository.findByEmail(userDetails.getUsername()).orElse(null)
                : null;

        // ▼▼▼ [추가] 비밀글 권한 확인
        if (qna.isSecret() && !isAuthorOrAdmin(qna, currentUser)) {
            throw new AuthorizationException("이 게시글을 조회할 권한이 없습니다.");
        }

        return QnaResponseDto.from(qna);
    }

    /**
     * Q&A 수정
     */
    @Transactional
    public QnaResponseDto updateQna(String userEmail, Long qnaId, QnaRequestDto requestDto) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ResourceNotFoundException("Q&A 게시글을 찾을 수 없습니다: " + qnaId));

        if (!qna.getUser().getId().equals(user.getId())) {
            throw new AuthorizationException("이 게시글을 수정할 권한이 없습니다.");
        }

        qna.setTitle(requestDto.title());
        qna.setContent(requestDto.content());
        qna.setSecret(requestDto.secret()); // ▼▼▼ [수정] secret 필드 업데이트

        Qna updatedQna = qnaRepository.save(qna);

        return QnaResponseDto.from(updatedQna);
    }

    /**
     * Q&A 삭제
     */
    @Transactional
    public void deleteQna(String userEmail, Long qnaId) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ResourceNotFoundException("Q&A 게시글을 찾을 수 없습니다: " + qnaId));

        // ▼▼▼ [수정] 관리자도 삭제할 수 있도록 권한 변경
        if (!isAuthorOrAdmin(qna, user)) {
            throw new AuthorizationException("이 게시글을 삭제할 권한이 없습니다.");
        }

        qnaRepository.delete(qna);
    }

    // ▼▼▼ [추가] 작성자 또는 관리자인지 확인하는 헬퍼 메서드
    private boolean isAuthorOrAdmin(Qna qna, Users user) {
        if (user == null) return false;
        if (user.getRole() == Role.ADMIN) return true;
        return qna.getUser().getId().equals(user.getId());
    }

    // ▼▼▼ [추가] 목록 조회 시 비밀글 내용을 숨기는 헬퍼 메서드
    private QnaResponseDto convertToDtoWithPermissionCheck(Qna qna, Users user) {
        // 비밀글인데 권한이 없는 경우 (작성자/관리자 아님)
        if (qna.isSecret() && !isAuthorOrAdmin(qna, user)) {
            return QnaResponseDto.builder()
                    .id(qna.getId())
                    .authorNickname(qna.getUser().getNickname())
                    .title(qna.getTitle())
                    .content("비밀글입니다. 작성자와 관리자만 조회할 수 있습니다.") // ▼ 내용 숨김
                    .createdAt(qna.getCreatedAt())
                    .updatedAt(qna.getUpdatedAt())
                    .secret(true)
                    .build();
        }

        // 공개글이거나 권한이 있는 경우
        return QnaResponseDto.from(qna);
    }
}