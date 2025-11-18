package com.passbee.mypage;

import com.passbee.mypage.dto.MyLicenseDtos;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users/me/licenses")
@RequiredArgsConstructor
public class MyLicenseController {

    private final MyLicenseService service;
    private final UsersRepository usersRepository; // 🔁 CurrentUserResolver 대신 직접 주입

    // 현재 로그인한 사용자의 userId 반환
    private Long currentUserIdOrThrow() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("UNAUTHORIZED");
        }
        String email = auth.getName();
        return usersRepository.findByEmail(email)
                .map(Users::getId)       // ✅ 보통 PK 게터명
                .orElseThrow(() -> new IllegalStateException("USER_NOT_FOUND: " + email));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<MyLicenseDtos.Item> list() {
        Long userId = currentUserIdOrThrow();
        return service.listMine(userId);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> add(@RequestBody @Valid MyLicenseDtos.Create req) {
        Long userId = currentUserIdOrThrow();
        Long id = service.add(userId, req);
        return ResponseEntity.created(URI.create("/api/users/me/licenses/" + id)).build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid MyLicenseDtos.Update req) {
        Long userId = currentUserIdOrThrow();
        service.update(userId, id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = currentUserIdOrThrow();
        service.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
