package com.passbee.license;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/licenses") // 이 컨트롤러의 모든 API는 /api/licenses로 시작
@RequiredArgsConstructor
@Tag(name = "Licenses", description = "자격증 정보 조회 API")
public class LicenseController {

    private final LicenseService licenseService;

    @GetMapping
    @Operation(summary = "전체 자격증 목록 조회")
    // 반환 타입을 License 엔티티 목록으로 설정합니다.
    public ResponseEntity<List<License>> getAllLicenses() {
        // 서비스에서 모든 License 엔티티를 조회합니다.
        List<License> licenses = licenseService.findAllLicenses();
        // 200 OK 상태 코드와 함께 자격증 목록 전체를 반환합니다.
        return ResponseEntity.ok(licenses);
    }
}