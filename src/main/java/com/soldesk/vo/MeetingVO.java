package com.soldesk.vo;

import java.time.LocalDateTime;

public class MeetingVO {
    private Long meeting_id; //회의 pk
    private Long project_id; //프로젝트 pk

    private String title; //회의명

    private LocalDateTime meet_date; //날짜/시간

    private String summary; //설명(한줄 요약)

    private String content; //내용(회의록)

    //get/set
    public Long getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(Long meeting_id) {
        this.meeting_id = meeting_id;
    }

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getMeet_date() {
        return meet_date;
    }

    public void setMeet_date(LocalDateTime meet_date) {
        this.meet_date = meet_date;
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
    
}
