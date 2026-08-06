package com.soldesk.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 멘토 대시보드 "멘토링 중인 팀" 카드 VO.
 * participation(project_role='MENTOR') 기준으로 프로젝트 + 팀원 수 + 업무 진행률 + 다음 회의를 한 번에 담는다.
 */
public class MentorTeamVO {

    private long projectId;
    private String title;
    private String category;
    private String status;           // 프로젝트 상태 (RECRUITING / CLOSED / DONE)
    private int memberCount;         // 멘토 제외 팀원 수 (LEADER + MEMBER)
    private int taskTotal;           // 전체 업무 수
    private int taskDone;            // 승인 완료(APPROVED)된 업무 수
    private LocalDateTime nextMeeting; // 다가오는 회의 일시 (없으면 null)

    public long getProjectId() { return projectId; }
    public void setProjectId(long projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getTaskTotal() { return taskTotal; }
    public void setTaskTotal(int taskTotal) { this.taskTotal = taskTotal; }

    public int getTaskDone() { return taskDone; }
    public void setTaskDone(int taskDone) { this.taskDone = taskDone; }

    public LocalDateTime getNextMeeting() { return nextMeeting; }
    public void setNextMeeting(LocalDateTime nextMeeting) { this.nextMeeting = nextMeeting; }

    /** 진행률(%) — 업무가 없으면 0 */
    public int getProgressPercent() {
        return taskTotal == 0 ? 0 : Math.round(taskDone * 100f / taskTotal);
    }

    /** 다음 회의 표시용 (예: 08.24 (월)) — MeetingVO 표기 컨벤션과 동일 */
    public String getNextMeetingDisplay() {
        return nextMeeting == null ? ""
                : nextMeeting.format(DateTimeFormatter.ofPattern("MM.dd (E)", Locale.KOREAN));
    }
}
