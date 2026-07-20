package com.soldesk.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskVO {
    private Long task_id; //업무pk
    private Long project_id; //프로젝트pk
    private Long assignee_id; //담당자 fk(member)

    private String task_name; //업무명
    private String description; //설명
    private String status; //상태(등록/진행/검수/승인/반려)

    private LocalDate due_date; //마감일

    private String submit_title; //제출제목
    private String submit_content; //제출내용
    private String submit_file; //제출물(파일경로)

    private String reject_reason; //반려 사유

    private LocalDateTime submitted_at; //제출시간


    //get/set
    public Long getTask_id() {
        return task_id;
    }
    public void setTask_id(Long task_id) {
        this.task_id = task_id;
    }
    public Long getProject_id() {
        return project_id;
    }
    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }
    public Long getAssignee_id() {
        return assignee_id;
    }
    public void setAssignee_id(Long assignee_id) {
        this.assignee_id = assignee_id;
    }
    public String getTask_name() {
        return task_name;
    }
    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDate getDue_date() {
        return due_date;
    }
    public void setDue_date(LocalDate due_date) {
        this.due_date = due_date;
    }
    public String getSubmit_title() {
        return submit_title;
    }
    public void setSubmit_title(String submit_title) {
        this.submit_title = submit_title;
    }
    public String getSubmit_content() {
        return submit_content;
    }
    public void setSubmit_content(String submit_content) {
        this.submit_content = submit_content;
    }
    public String getSubmit_file() {
        return submit_file;
    }
    public void setSubmit_file(String submit_file) {
        this.submit_file = submit_file;
    }
    public String getReject_reason() {
        return reject_reason;
    }
    public void setReject_reason(String reject_reason) {
        this.reject_reason = reject_reason;
    }
    public LocalDateTime getSubmitted_at() {
        return submitted_at;
    }
    public void setSubmitted_at(LocalDateTime submitted_at) {
        this.submitted_at = submitted_at;
    }
    
}
