package com.passbee.ranking;

import com.passbee.ranking.dto.LicenseRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingQueryService service;

    @GetMapping("/licenses")
    public LicenseRankingResponse getLicenseRankings(
            @RequestParam(defaultValue = "applications") String metric,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "20") int limit
    ){
        return service.getRankings(metric, YearMonth.of(year, month), Math.min(limit, 100));
    }
}
