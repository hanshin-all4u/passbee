package com.passbee.ranking.dto;

import java.math.BigDecimal;
import java.util.List;

public record LicenseRankingResponse(
        String metric, String period, List<Item> items
) {
    public record Item(
            String jmcd, String name, int rank,
            Integer applications, BigDecimal passRate,
            Integer favorites, Integer reviews
    ){}
}
