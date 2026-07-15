package com.soldesk.vo;

import java.time.LocalDateTime;

public class MeetingVO {
    private Long meetingId; //회의 pk
    private Long projectId; //프로젝트 pk

    private String title; //회의명

    private LocalDateTime meetDate; //날짜/시간

    private String summary; //설명(한줄 요약)

    private String content; //내용(회의록)

    private LocalDateTime createdAt; //timestamp

    //getter,setter
    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getMeetDate() {
        return meetDate;
    }

    public void setMeetDate(LocalDateTime meetDate) {
        this.meetDate = meetDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
