package com.passbee.favorite.dto;

import com.passbee.favorite.domain.Favorite;
import com.passbee.favorite.domain.FavoriteType;

import java.time.LocalDateTime;
import java.util.List;

public class FavoriteDtos {

    // 추가 요청
    public record FavoriteAddRequest(FavoriteType type, Long targetId) {}

    // 단건 응답
    public record FavoriteResponse(Long id, FavoriteType type, Long targetId, LocalDateTime createdAt) {
        public static FavoriteResponse from(Favorite f) {
            return new FavoriteResponse(f.getId(), f.getTargetType(), f.getTargetId(), f.getCreatedAt());
        }
    }

    // 목록 응답
    public record FavoriteListResponse(List<FavoriteResponse> items, int page, int size, long totalElements) {}

    // 상태(내가 즐겨찾기 했는지 + 전체 카운트)
    public record FavoriteStatusResponse(boolean favorited, long favoriteCount) {}
}
