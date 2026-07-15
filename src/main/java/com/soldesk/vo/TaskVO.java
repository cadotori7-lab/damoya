package com.soldesk.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskVO {
    private Long taskId; //업무pk
    private Long projectId; //프로젝트pk
    private Long assigneeId; //담당자 fk(member)

    private String taskName; //업무명
    private String description; //설명
    private String status; //상태(등록/진행/검수/승인/반려)

    private LocalDate dueDate; //마감일

    private String submitTitle; //제출제목
    private String submitContent; //제출내용
    private String submitFile; //제출물(파일경로)

    private String rejectReason; //반려 사유

    private LocalDateTime submittedAt; //제출시간
    private LocalDateTime createdAt; //timestamp

    //getter,setter
    public Long getTaskId() {
        return taskId;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public Long getAssigneeId() {
        return assigneeId;
    }
    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
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
    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public String getSubmitTitle() {
        return submitTitle;
    }
    public void setSubmitTitle(String submitTitle) {
        this.submitTitle = submitTitle;
    }
    public String getSubmitContent() {
        return submitContent;
    }
    public void setSubmitContent(String submitContent) {
        this.submitContent = submitContent;
    }
    public String getSubmitFile() {
        return submitFile;
    }
    public void setSubmitFile(String submitFile) {
        this.submitFile = submitFile;
    }
    public String getRejectReason() {
        return rejectReason;
    }
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
