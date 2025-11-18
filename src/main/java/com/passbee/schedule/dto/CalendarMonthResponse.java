package com.passbee.schedule.dto;

import java.util.List;

public record CalendarMonthResponse(
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
            String name,   // 지금은 jmcd 그대로 넣어두고, 나중에 LicenseService로 이름 매핑
            String type,   // REG_OPEN, REG_CLOSE, PI_EXAM, SI_EXAM, RESULT ...
            String badge,  // 접수/마감/필기/실기/발표
            String link    // ex) /licenses/{jmcd}
    ) {}
}
