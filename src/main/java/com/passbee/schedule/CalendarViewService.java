package com.passbee.schedule;

import com.passbee.schedule.dto.CalendarCellResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarViewService {

    private final ExamScheduleRepository repo; // ✅ 리포지토리 직접 사용

    public CalendarCellResponse buildMonthCells(int year, int month, int limitPerDay,
                                                String jmcd, boolean onlyFavorites) {
        var ym = YearMonth.of(year, month);

        var rows = (jmcd != null && !jmcd.isBlank())
                ? repo.findByJmcdAndDateBetween(jmcd, ym.atDay(1), ym.atEndOfMonth())
                : repo.findByDateBetween(ym.atDay(1), ym.atEndOfMonth());

        // NOTE: onlyFavorites=true 실제 적용은 나중에 즐겨찾기 연동 시 반영
        var byDay = rows.stream().collect(Collectors.groupingBy(r -> r.getDate().getDayOfMonth()));

        List<CalendarCellResponse.Day> days = new ArrayList<>(ym.lengthOfMonth());
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            var list = byDay.getOrDefault(d, List.of());
            int total = list.size();
            var items = list.stream()
                    .sorted(Comparator
                            .comparing((ExamSchedule r) -> priority(r.getType()))
                            .thenComparing(r -> resolveName(r.getJmcd())))
                    .limit(Math.max(1, limitPerDay))
                    .map(r -> new CalendarCellResponse.Item(
                            r.getJmcd(),
                            resolveName(r.getJmcd()),
                            r.getType().name(),
                            toBadge(r.getType()),
                            "/licenses/" + r.getJmcd()  // 클릭 이동 링크
                    )).toList();

            days.add(new CalendarCellResponse.Day(d, items, total));
        }
        return new CalendarCellResponse(year, month, days);
    }

    // TODO: 나중에 LicenseRepository 연결
    private String resolveName(String jmcd) { return jmcd; }

    private int priority(ExamSchedule.ScheduleType t) {
        return switch (t) {
            case REG_CLOSE -> 0;
            case PI_EXAM, SI_EXAM -> 1;
            case REG_OPEN -> 2;
            case RESULT -> 3;
        };
    }

    private String toBadge(ExamSchedule.ScheduleType t) {
        return switch (t) {
            case REG_OPEN -> "접수";
            case REG_CLOSE -> "마감";
            case PI_EXAM -> "필기";
            case SI_EXAM -> "실기";
            case RESULT -> "발표";
        };
    }
}
