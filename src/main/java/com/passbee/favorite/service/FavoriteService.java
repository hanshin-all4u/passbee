package com.passbee.favorite.service;

import com.passbee.favorite.domain.Favorite;
import com.passbee.favorite.domain.FavoriteType;
import com.passbee.favorite.dto.FavoriteDtos.*;
import com.passbee.favorite.repo.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteResponse add(FavoriteAddRequest req, Long userId) {
        // 멱등성: 이미 있으면 기존 반환
        var existing = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(userId, req.type(), req.targetId());
        if (existing.isPresent()) return FavoriteResponse.from(existing.get());

        try {
            var saved = favoriteRepository.save(Favorite.builder()
                    .userId(userId)
                    .targetType(req.type())
                    .targetId(req.targetId())
                    .build());
            return FavoriteResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            // 레이스 컨디션 대비: 유니크 제약에 걸리면 기존값 재조회하여 반환
            var dup = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(userId, req.type(), req.targetId())
                    .orElseThrow(() -> e);
            return FavoriteResponse.from(dup);
        }
    }

    public void remove(FavoriteType type, Long targetId, Long userId) {
        favoriteRepository.deleteByUserIdAndTargetTypeAndTargetId(userId, type, targetId);
    }

    @Transactional(readOnly = true)
    public FavoriteListResponse list(FavoriteType type, int page, int size, Long userId) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        var result = favoriteRepository.findByUserIdAndTargetType(userId, type, pageable);
        var items = result.stream().map(FavoriteResponse::from).toList();
        return new FavoriteListResponse(items, page, size, result.getTotalElements());
    }

    @Transactional(readOnly = true)
    public FavoriteStatusResponse status(FavoriteType type, Long targetId, Long userId) {
        boolean favorited = favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, type, targetId);
        long count = favoriteRepository.countByTargetTypeAndTargetId(type, targetId);
        return new FavoriteStatusResponse(favorited, count);
    }
}
