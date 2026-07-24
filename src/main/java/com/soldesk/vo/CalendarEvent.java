package com.soldesk.vo;

import java.util.Date;

/**
 * 캘린더에 찍히는 일정 하나.
 * 업무 마감(task) / 회의(meet) / 멘토 피드백(fb) 을 한 형태로 모은다.
 * type 값이 그대로 CSS 클래스(.ev.task / .ev.meet / .ev.fb)가 된다.
 */
public class CalendarEvent {

    private String type;    // task | meet | fb
    private String title;   // 표시 문구
    private Date   evDate;  // 해당 날짜
    private Long   refId;   // 원본 id (업무/회의 상세로 이동할 때 사용)

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getEvDate() { return evDate; }
    public void setEvDate(Date evDate) { this.evDate = evDate; }

    public Long getRefId() { return refId; }
    public void setRefId(Long refId) { this.refId = refId; }
}
