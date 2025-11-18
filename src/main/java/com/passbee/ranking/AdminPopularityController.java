package com.passbee.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rankings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPopularityController {

    private final PopularityMetricsRepository repo;
    private final PopularitySources sources;

    @PostMapping("/rebuild")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rebuild(@RequestParam int year, @RequestParam int month){
        var ym = YearMonth.of(year, month);
        var period = ym.atDay(1);
        var jmcds = sources.listAllJmcds();

        for (String jmcd : jmcds) {
            int apps = sources.getMonthlyApplications(jmcd, ym);
            BigDecimal pr = sources.getMonthlyPassRate(jmcd, ym);
            int fav = sources.countFavorites(jmcd);
            int rev = sources.countReviewsInMonth(jmcd, ym);

            var e = repo.findByPeriod(period, org.springframework.data.domain.PageRequest.of(0,1))
                    .stream().filter(pm -> pm.getJmcd().equals(jmcd)).findFirst()
                    .orElse(PopularityMetrics.builder().jmcd(jmcd).period(period).build());

            e.setApplications(apps);
            e.setPassRate(pr);
            e.setFavorites(fav);
            e.setReviews(rev);
            repo.save(e);
        }
    }

    /** 간단한 소스 어댑터 (네 서비스들에 맞게 구현해서 @Component로 제공) */
    public interface PopularitySources {
        List<String> listAllJmcds();
        int getMonthlyApplications(String jmcd, YearMonth ym);
        BigDecimal getMonthlyPassRate(String jmcd, YearMonth ym);
        int countFavorites(String jmcd);
        int countReviewsInMonth(String jmcd, YearMonth ym);
    }
}
