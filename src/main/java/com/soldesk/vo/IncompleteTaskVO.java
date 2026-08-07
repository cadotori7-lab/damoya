package com.soldesk.vo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** 프로젝트 완료를 막고 있는 업무를 표시하기 위한 조회 전용 객체. */
public class IncompleteTaskVO {

    private static final DateTimeFormatter DATE_DISPLAY =
            DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private Long taskId;
    private String assigneeName;
    private String taskName;
    private LocalDate dueDate;
    private String status;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDateLabel() {
        return dueDate == null ? "-" : dueDate.format(DATE_DISPLAY);
    }

    public String getStatusLabel() {
        if ("REVIEW".equalsIgnoreCase(status)) {
            return "검수 대기";
        }
        if ("REJECTED".equalsIgnoreCase(status)) {
            return "반려";
        }
        if ("APPROVED".equalsIgnoreCase(status)) {
            return "완료";
        }
        return "진행 중";
    }

    public String getStatusClass() {
        if ("REVIEW".equalsIgnoreCase(status)) {
            return "wait";
        }
        if ("REJECTED".equalsIgnoreCase(status)) {
            return "reject";
        }
        if ("APPROVED".equalsIgnoreCase(status)) {
            return "approve";
        }
        return "ing";
    }
}
