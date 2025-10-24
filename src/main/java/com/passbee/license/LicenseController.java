package com.passbee.license;

import com.passbee.license.dto.LicenseDetailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // ▼▼▼ [추가] Sort import
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
@Tag(name = "Licenses", description = "자격증 정보 조회 API")
public class LicenseController {

    private final LicenseService licenseService;

    @GetMapping
    @Operation(summary = "자격증 목록 검색/조회 (페이징)",
            description = "keyword(검색어)와 seriesnm(분야) 파라미터를 사용하여 검색합니다.")
    public ResponseEntity<Page<License>> searchLicenses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String seriesnm,
            // ▼▼▼ [수정] sort와 direction을 분리하고, 기본 정렬 필드를 jmfldnm으로 변경 ▼▼▼
            @PageableDefault(size = 10, sort = "jmfldnm", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<License> licensesPage = licenseService.searchLicenses(keyword, seriesnm, pageable);
        return ResponseEntity.ok(licensesPage);
    }

    @GetMapping("/{jmcd}")
    @Operation(summary = "자격증 상세 정보 조회 (시험과목, 지참물 포함)")
    public ResponseEntity<LicenseDetailResponseDto> getLicenseDetails(
            @PathVariable String jmcd) {

        LicenseDetailResponseDto licenseDetails = licenseService.getLicenseDetails(jmcd);
        return ResponseEntity.ok(licenseDetails);
    }
}