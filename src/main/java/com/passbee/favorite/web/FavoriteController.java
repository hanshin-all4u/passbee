package com.passbee.favorite.web;

import com.passbee.common.security.AuthUtils;
import com.passbee.favorite.domain.FavoriteType;
import com.passbee.favorite.dto.FavoriteDtos.*;
import com.passbee.favorite.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<FavoriteResponse> add(@RequestBody @Valid FavoriteAddRequest req) {
        Long userId = AuthUtils.currentUserId();
        var res = favoriteService.add(req, userId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(@RequestParam FavoriteType type, @RequestParam Long targetId) {
        Long userId = AuthUtils.currentUserId();
        favoriteService.remove(type, targetId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public FavoriteListResponse list(@RequestParam FavoriteType type,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        Long userId = AuthUtils.currentUserId();
        return favoriteService.list(type, page, size, userId);
    }

    @GetMapping("/status")
    public FavoriteStatusResponse status(@RequestParam FavoriteType type, @RequestParam Long targetId) {
        Long userId = AuthUtils.currentUserId();
        return favoriteService.status(type, targetId, userId);
    }
}
