package com.passbee.schedule;

import com.passbee.schedule.dto.ScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Schedules", description = "시험 일정 캘린더 조회")
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleQueryService service;

    @Operation(summary = "월간 시험 일정 조회",
            description = "지정 월의 시험 일정(접수/시험/발표)을 조회합니다.")
    @GetMapping
    public ScheduleResponse getMonthly(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) String jmcd,
            @RequestParam(required = false) String types, // CSV: REG_OPEN,PI_EXAM
            @RequestParam(defaultValue = "false") boolean onlyFavorites
    ) {
        List<ExamSchedule.ScheduleType> typeList = null;
        if (types != null && !types.isBlank()) {
            typeList = Arrays.stream(types.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(ExamSchedule.ScheduleType::valueOf)
                    .toList();
        }
        return service.getMonthly(year, month, jmcd, typeList, onlyFavorites);
    }
}