package com.passbee.favorite.repo;

import com.passbee.favorite.domain.Favorite;
import com.passbee.favorite.domain.FavoriteType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, FavoriteType type, Long targetId);

    Optional<Favorite> findByUserIdAndTargetTypeAndTargetId(Long userId, FavoriteType type, Long targetId);

    Page<Favorite> findByUserIdAndTargetType(Long userId, FavoriteType type, Pageable pageable);

    long countByTargetTypeAndTargetId(FavoriteType type, Long targetId);

    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, FavoriteType type, Long targetId);
}
