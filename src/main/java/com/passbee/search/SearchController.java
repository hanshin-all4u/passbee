package com.passbee.search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Search", description = "통합 검색 API")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    @Operation(summary = "통합 검색 (자격증, 후기, 게시글 등)")
    public ResponseEntity<List<IntegratedSearchResultDto>> search(@RequestParam String query) {
        List<IntegratedSearchResultDto> results = searchService.searchAll(query);
        return ResponseEntity.ok(results);
    }
}