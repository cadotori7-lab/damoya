package com.soldesk.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 달력 칸 하나.
 * 서비스가 이걸 7개씩 묶어 주 리스트로 만들어 넘기면
 * JSP 는 이중 forEach 만 돌면 된다.
 */
public class CalendarCell {

    private int day;                    // 칸에 표시할 날짜 숫자
    private boolean outside;            // 이전/다음 달 칸이면 true (.cell.out)
    private boolean today;              // 오늘이면 true (.cell.today)
    private List<CalendarEvent> events = new ArrayList<>();

    public CalendarCell(int day, boolean outside, boolean today) {
        this.day = day;
        this.outside = outside;
        this.today = today;
    }

    public int getDay() { return day; }
    public boolean isOutside() { return outside; }
    public boolean isToday() { return today; }

    public List<CalendarEvent> getEvents() { return events; }
    public void setEvents(List<CalendarEvent> events) { this.events = events; }
}
