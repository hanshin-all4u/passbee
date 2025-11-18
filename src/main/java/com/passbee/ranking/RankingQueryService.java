package com.passbee.ranking;

import com.passbee.ranking.dto.LicenseRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankingQueryService {

    private final PopularityMetricsRepository repo;

    private static final Set<String> METRICS = Set.of("applications","passRate","favorites","reviews");

    public LicenseRankingResponse getRankings(String metric, YearMonth ym, int limit){
        if(!METRICS.contains(metric)) throw new IllegalArgumentException("invalid metric");

        var sort = switch (metric){
            case "applications" -> Sort.by(Sort.Direction.DESC, "applications");
            case "passRate"     -> Sort.by(Sort.Direction.DESC, "passRate");
            case "favorites"    -> Sort.by(Sort.Direction.DESC, "favorites");
            default             -> Sort.by(Sort.Direction.DESC, "reviews");
        };

        var period = ym.atDay(1);
        var rows = repo.findByPeriod(period, PageRequest.of(0, Math.min(limit, 100), sort));

        var items = new ArrayList<LicenseRankingResponse.Item>();
        int rank = 1;
        for (var r : rows) {
            var name = resolveName(r.getJmcd()); // ← 임시 이름 해석
            items.add(new LicenseRankingResponse.Item(
                    r.getJmcd(), name, rank++,
                    r.getApplications(), r.getPassRate(),
                    r.getFavorites(), r.getReviews()
            ));
        }
        return new LicenseRankingResponse(metric, ym.toString(), items);
    }

    /** TODO: 실제 License 이름 조회 서비스로 교체하세요. */
    private String resolveName(String jmcd) {
        return jmcd; // 임시: 이름 데이터가 없을 때는 jmcd 그대로 표기
    }
}