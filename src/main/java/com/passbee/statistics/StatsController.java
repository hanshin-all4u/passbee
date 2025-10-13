package com.passbee.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Statistics", description = "통계 수집 API (DB 저장)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "특정 연도의 모든 통계 수집/저장")
    @PostMapping("/import-year")
    public String importYear(@RequestParam int year) {
        statsService.importYear(year);
        return "saved stats for year: " + year;
    }

    @Operation(summary = "연도 범위 일괄 수집/저장 (포함 범위)")
    @PostMapping("/import-range")
    public String importRange(@RequestParam int from, @RequestParam int to) {
        statsService.importRange(from, to);
        return "saved stats for years: " + from + ".." + to;
    }
}


