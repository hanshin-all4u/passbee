package com.passbee.search;

// ▼▼▼ [수정] DTO import 경로 확인 (search 패키지 바로 아래) ▼▼▼
import com.passbee.search.IntegratedSearchResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
// ▼▼▼ [추가] Pageable 관련 import ▼▼▼
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // Sort import 추가
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// ▼▼▼ [수정] RequestMapping 경로 확인 (기존 코드 유지) ▼▼▼
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Search", description = "통합 검색 API")
public class SearchController {

    private final SearchService searchService;

    // ▼▼▼ [수정] 메서드 시그니처와 서비스 호출 부분 수정 ▼▼▼
    @GetMapping("/search")
    @Operation(summary = "통합 검색 (자격증, 후기 등)")
    public ResponseEntity<List<IntegratedSearchResultDto>> search(
            @RequestParam String query,
            // 1. Pageable 파라미터 추가
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // 2. searchService.searchAll 호출 시 pageable 전달
        List<IntegratedSearchResultDto> results = searchService.searchAll(query, pageable);
        return ResponseEntity.ok(results);
    }
}