package com.passbee.license;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // <-- 이 부분이 추가되었습니다!
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
@Tag(name = "Licenses", description = "자격증 정보 조회 API")
public class LicenseController {

    private final LicenseService licenseService;

    @GetMapping
    @Operation(summary = "전체 자격증 목록 조회")
    public ResponseEntity<List<License>> getAllLicenses() {
        List<License> licenses = licenseService.findAllLicenses();
        return ResponseEntity.ok(licenses);
    }

    @GetMapping("/search")
    @Operation(summary = "자격증 이름으로 검색")
    public ResponseEntity<List<License>> searchLicenses(@RequestParam String keyword) {
        List<License> licenses = licenseService.searchLicenses(keyword);
        return ResponseEntity.ok(licenses);
    }
}