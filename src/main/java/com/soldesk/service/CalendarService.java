package com.soldesk.service;

import com.soldesk.mapper.ScheduleMapper;
import com.soldesk.vo.CalendarCell;
import com.soldesk.vo.CalendarEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;

@Service
public class CalendarService {

    @Autowired private ScheduleMapper scheduleMapper;

    public List<List<CalendarCell>> buildMonth(long projectId, YearMonth ym) {

        LocalDate first = ym.atDay(1);
        LocalDate last  = ym.atEndOfMonth();

        List<CalendarEvent> events = scheduleMapper.selectMonthEvents(
                projectId,
                Timestamp.valueOf(first.atStartOfDay()),
                Timestamp.valueOf(last.atTime(23, 59, 59))
        );

        Map<Integer, List<CalendarEvent>> byDay = new HashMap<>();
        for (CalendarEvent e : events) {
            if (e.getEvDate() == null) continue;
            LocalDate d = Instant.ofEpochMilli(e.getEvDate().getTime())
                                 .atZone(ZoneId.systemDefault())
                                 .toLocalDate();
            byDay.computeIfAbsent(d.getDayOfMonth(), k -> new ArrayList<>()).add(e);
        }

        int firstDow   = first.getDayOfWeek().getValue() % 7;
        int lengthOfMonth = ym.lengthOfMonth();
        int prevLength = ym.minusMonths(1).lengthOfMonth();

        LocalDate today = LocalDate.now();
        boolean isThisMonth = ym.equals(YearMonth.from(today));

        List<CalendarCell> cells = new ArrayList<>();

        // 앞쪽 빈 칸 (지난달 끝자락)
        for (int i = 0; i < firstDow; i++) {
            cells.add(new CalendarCell(prevLength - firstDow + i + 1, true, false));
        }

        // 이번 달
        for (int d = 1; d <= lengthOfMonth; d++) {
            boolean isToday = isThisMonth && today.getDayOfMonth() == d;
            CalendarCell cell = new CalendarCell(d, false, isToday);
            cell.setEvents(byDay.getOrDefault(d, Collections.emptyList()));
            cells.add(cell);
        }

        // 뒤쪽 빈 칸 (다음달 시작)
        int rem = cells.size() % 7;
        if (rem != 0) {
            for (int i = 1; i <= 7 - rem; i++) {
                cells.add(new CalendarCell(i, true, false));
            }
        }

        // 7칸씩 잘라 주 단위로
        List<List<CalendarCell>> weeks = new ArrayList<>();
        for (int i = 0; i < cells.size(); i += 7) {
            weeks.add(new ArrayList<>(cells.subList(i, i + 7)));
        }
        return weeks;
    }
}
