package com.passbee.favorite.web;

import com.passbee.license.License;
import com.passbee.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Tag(name = "Favorites", description = "즐겨찾기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites/licenses") // 자격증 즐겨찾기 경로 명시
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "자격증 즐겨찾기 추가")
    @PostMapping("/{jmcd}")
    public ResponseEntity<?> addFavorite(@PathVariable String jmcd, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            favoriteService.addLicenseFavorite(userDetails.getUsername(), jmcd);
            return ResponseEntity.ok(Map.of("message", "즐겨찾기에 추가되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "자격증 즐겨찾기 삭제")
    @DeleteMapping("/{jmcd}")
    public ResponseEntity<?> removeFavorite(@PathVariable String jmcd, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            favoriteService.removeLicenseFavorite(userDetails.getUsername(), jmcd);
            return ResponseEntity.ok(Map.of("message", "즐겨찾기에서 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "내 자격증 즐겨찾기 목록 조회")
    @GetMapping
    public ResponseEntity<?> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            List<License> favorites = favoriteService.getLicenseFavorites(userDetails.getUsername());
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}