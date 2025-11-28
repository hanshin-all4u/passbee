package com.passbee.ranking;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * 임시 구현: 서버 부팅을 막지 않도록 0/빈값을 돌려주는 No-Op.
 * 나중에 실제 통계/즐겨찾기/후기 소스와 연결해 교체하세요.
 */
@Component
public class PopularitySourcesNoop implements AdminPopularityController.PopularitySources {

    @Override
    public List<String> listAllJmcds() {
        // TODO: License 테이블이 있으면 여기서 jmcd 전체를 조회하도록 교체
        return List.of(); // 지금은 비워두면 /rebuild가 아무 작업도 안 함
    }

    @Override
    public int getMonthlyApplications(String jmcd, YearMonth ym) {
        return 0;
    }

    @Override
    public BigDecimal getMonthlyPassRate(String jmcd, YearMonth ym) {
        return BigDecimal.ZERO; // 0~1 스케일
    }

    @Override
    public int countFavorites(String jmcd) {
        return 0;
    }

    @Override
    public int countReviewsInMonth(String jmcd, YearMonth ym) {
        return 0;
    }
}