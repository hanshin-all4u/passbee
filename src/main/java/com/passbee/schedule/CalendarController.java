package com.passbee.schedule;

import com.passbee.schedule.dto.CalendarCellResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarViewService service;

    // 예: /api/schedules/calendar?year=2026&month=3&limitPerDay=3
    @GetMapping("/calendar")
    public CalendarCellResponse calendar(@RequestParam int year,
                                         @RequestParam int month,
                                         @RequestParam(defaultValue = "3") int limitPerDay,
                                         @RequestParam(required = false) String jmcd,
                                         @RequestParam(defaultValue = "false") boolean onlyFavorites) {
        return service.buildMonthCells(year, month, limitPerDay, jmcd, onlyFavorites);
    }
}