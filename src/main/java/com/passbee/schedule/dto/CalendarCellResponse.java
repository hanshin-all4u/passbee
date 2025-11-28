package com.passbee.schedule.dto;

import java.util.List;

public record CalendarCellResponse(
        int year,
        int month,
        List<Day> days
) {
    public record Day(
            int day,
            List<Item> items,
            int total
    ) {}

    public record Item(
            String jmcd,
            String name,     // TODO: 나중에 jmcd → 한글명 매핑
            String type,     // REG_OPEN / REG_CLOSE / PI_EXAM / SI_EXAM / RESULT
            String badge,    // 접수 / 마감 / 필기 / 실기 / 발표
            String link      // ex) /licenses/{jmcd}
    ) {}
}