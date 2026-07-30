package com.soldesk.vo;

/** 멘토 대시보드 상단 통계 */
public class MentorDashboardStatsVO {
    private int mentoringTeamCount;   // 멘토링 중인 팀
    private int activeProjectCount;   // 진행 중 프로젝트
    private int feedbackCount;        // 내가 남긴 피드백

    public int getMentoringTeamCount() {
        return mentoringTeamCount;
    }

    public void setMentoringTeamCount(int mentoringTeamCount) {
        this.mentoringTeamCount = mentoringTeamCount;
    }

    public int getActiveProjectCount() {
        return activeProjectCount;
    }

    public void setActiveProjectCount(int activeProjectCount) {
        this.activeProjectCount = activeProjectCount;
    }

    public int getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(int feedbackCount) {
        this.feedbackCount = feedbackCount;
    }
}
