package com.passbee.schedule.dto;

import java.time.LocalDate;
import java.util.List;

public record ScheduleResponse(
        int year,
        int month,
        List<Item> items
) {
    public record Item(
            String jmcd,
            String name,            // jmcd → 자격증 이름 (없으면 jmcd 그대로)
            LocalDate date,        // 일정 날짜
            String type,           // REG_OPEN | REG_CLOSE | PI_EXAM | SI_EXAM | RESULT
            String badge,          // "접수", "마감", "필기", "실기", "발표"
            RegWindow regWindow,   // 같은 jmcd의 해당 월 open/close 묶어둔 값 (선택)
            Fee fee,               // 응시료 정보 (선택)
            String applyUrl,       // 큐넷 접수 URL 등
            String note,           // 비고
            boolean favorite,      // 즐겨찾기 여부
            String status,         // "PAST" | "TODAY" | "UPCOMING"
            Integer dday           // 오늘 기준 날짜 차이 (예: D-3 -> 3, D+2 -> -2, 오늘 0)
    ) {}

    public record RegWindow(String open, String close) {}
    public record Fee(Integer pi, Integer si) {}
}
