package com.passbee.schedule;

import com.passbee.schedule.dto.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleQueryService {

    private final ExamScheduleRepository repo;

    // TODO: 실제 구현으로 교체 (jmcd -> name)
    private String resolveName(String jmcd) {
        return jmcd;
    }

    // TODO: 실제 구현으로 교체 (jmcd -> fee)
    private ScheduleResponse.Fee resolveFee(String jmcd) {
        return null;
    }

    // TODO: 로그인한 유저의 즐겨찾기 여부 (jmcd 세트)
    private Set<String> myFavoritesOrEmpty(boolean onlyFavorites) {
        return Collections.emptySet();
    }

    public ScheduleResponse getMonthly(Integer year, Integer month,
                                       String jmcd,
                                       List<ExamSchedule.ScheduleType> types,
                                       boolean onlyFavorites) {

        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to   = ym.atEndOfMonth();

        List<ExamSchedule> rows;
        if (jmcd != null && !jmcd.isBlank()) {
            rows = (types == null || types.isEmpty())
                    ? repo.findByJmcdAndDateBetween(jmcd, from, to)
                    : repo.findByJmcdAndDateBetweenAndTypeIn(jmcd, from, to, types);
        } else {
            rows = (types == null || types.isEmpty())
                    ? repo.findByDateBetween(from, to)
                    : repo.findByDateBetweenAndTypeIn(from, to, types);
        }

        // 즐겨찾기 필터링
        Set<String> myFavs = myFavoritesOrEmpty(onlyFavorites);
        if (onlyFavorites && !myFavs.isEmpty()) {
            rows = rows.stream()
                    .filter(r -> myFavs.contains(r.getJmcd()))
                    .toList();
        } else if (onlyFavorites) {
            rows = List.of(); // 로그인 전 or 즐겨찾기 없음
        }

        // 같은 jmcd의 해당 월 REG_OPEN/REG_CLOSE를 한 쌍으로 찾아 RegWindow로 넣어줌
        Map<String, Map<String, String>> regWindowMap = buildRegWindowMap(rows);

        LocalDate today = LocalDate.now(); // 기준은 서버 날짜 (필요하면 Asia/Seoul로 변경)

        var items = rows.stream()
                .map(r -> {
                    String name = resolveName(r.getJmcd());

                    String badge = switch (r.getType()) {
                        case REG_OPEN -> "접수";
                        case REG_CLOSE -> "마감";
                        case PI_EXAM -> "필기";
                        case SI_EXAM -> "실기";
                        case RESULT -> "발표";
                    };

                    ScheduleResponse.RegWindow rw = null;
                    Map<String, String> rwPair = regWindowMap.getOrDefault(r.getJmcd(), Map.of());
                    if (rwPair.containsKey("open") || rwPair.containsKey("close")) {
                        rw = new ScheduleResponse.RegWindow(
                                rwPair.get("open"),
                                rwPair.get("close")
                        );
                    }

                    boolean fav = myFavs.contains(r.getJmcd());

                    LocalDate scheduleDate = r.getDate();
                    long diff = ChronoUnit.DAYS.between(today, scheduleDate); // scheduleDate - today

                    String status;
                    if (diff < 0) {
                        status = "PAST";
                    } else if (diff == 0) {
                        status = "TODAY";
                    } else {
                        status = "UPCOMING";
                    }
                    int dday = (int) diff;

                    return new ScheduleResponse.Item(
                            r.getJmcd(),
                            name,
                            scheduleDate,
                            r.getType().name(),
                            badge,
                            rw,
                            resolveFee(r.getJmcd()),
                            r.getApplyUrl(),
                            r.getNote(),
                            fav,
                            status,
                            dday
                    );
                })
                .sorted(Comparator.comparing(ScheduleResponse.Item::date)
                        .thenComparing(ScheduleResponse.Item::type))
                .collect(Collectors.toList());

        return new ScheduleResponse(ym.getYear(), ym.getMonthValue(), items);
    }

    private Map<String, Map<String, String>> buildRegWindowMap(List<ExamSchedule> rows) {
        Map<String, Map<String, String>> map = new HashMap<>();
        for (var r : rows) {
            if (r.getType() == ExamSchedule.ScheduleType.REG_OPEN) {
                map.computeIfAbsent(r.getJmcd(), k -> new HashMap<>())
                        .put("open", r.getDate().toString());
            } else if (r.getType() == ExamSchedule.ScheduleType.REG_CLOSE) {
                map.computeIfAbsent(r.getJmcd(), k -> new HashMap<>())
                        .put("close", r.getDate().toString());
            }
        }
        return map;
    }
}