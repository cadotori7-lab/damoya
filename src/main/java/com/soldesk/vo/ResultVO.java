package com.soldesk.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** 승인된 업무 제출물을 결과물 화면에 표시하기 위한 조회 전용 객체. */
public class ResultVO {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MM.dd");

    private Long taskId;
    private Long projectId;
    private Long assigneeId;
    private String taskName;
    private String submitTitle;
    private String submitContent;
    private String submitFile;
    private LocalDateTime submittedAt;
    private String assigneeName;

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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getOriginalFileName() {
        if (submitFile == null || submitFile.trim().isEmpty()) {
            return "";
        }

        int separator = submitFile.indexOf('_');
        return separator >= 0 && separator < submitFile.length() - 1
                ? submitFile.substring(separator + 1)
                : submitFile;
    }

    public String getFileTypeClass() {
        String fileName = getOriginalFileName().toLowerCase(Locale.ROOT);
        int dot = fileName.lastIndexOf('.');
        String extension = dot >= 0 ? fileName.substring(dot + 1) : "";

        if (extension.matches("png|jpg|jpeg|gif|webp")) {
            return "img";
        }
        if (extension.matches("ppt|pptx")) {
            return "deck";
        }
        if (extension.matches("zip|rar|7z")) {
            return "zip";
        }
        return "doc";
    }

    public String getSubmittedDateLabel() {
        return submittedAt == null ? "" : submittedAt.format(DATE_FORMAT);
    }

    public String getAssigneeInitial() {
        if (assigneeName == null || assigneeName.trim().isEmpty()) {
            return "?";
        }
        return assigneeName.trim().substring(0, 1);
    }
}
