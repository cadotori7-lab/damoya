package com.soldesk.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** feedback 테이블 VO (멘토가 프로젝트에 남기는 단계별 피드백) */
public class FeedbackVO {

    private Long feedbackId;
    private Long projectId;
    private Long mentorId;
    private String content;
    private String stage;          // MIDTERM(중간 점검) / FINAL(최종 평가) / REVIEW(완료 검토)
    private String feedbackFile;   // 첨부 파일 경로 (1단계에서는 미사용)
    private LocalDateTime createdAt;

    private String projectTitle;   // 목록 표시용 (project JOIN)

    public Long getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Long feedbackId) { this.feedbackId = feedbackId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getFeedbackFile() { return feedbackFile; }
    public void setFeedbackFile(String feedbackFile) { this.feedbackFile = feedbackFile; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    /** 화면 표시용 단계 라벨 */
    public String getStageLabel() {
        if ("MIDTERM".equals(stage)) return "중간 점검";
        if ("FINAL".equals(stage)) return "최종 평가";
        if ("REVIEW".equals(stage)) return "완료 검토";
        return stage;
    }

    /** 작성일 표시 (예: 2026.08.06) */
    public String getCreatedAtDisplay() {
        return createdAt == null ? ""
                : createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
