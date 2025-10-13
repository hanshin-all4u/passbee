package com.passbee.favorite.repo;

import com.passbee.favorite.domain.Favorite;
import com.passbee.favorite.domain.FavoriteType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, FavoriteType type, String targetId);
    Optional<Favorite> findByUserIdAndTargetTypeAndTargetId(Long userId, FavoriteType type, String targetId);
    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, FavoriteType type, String targetId);
}