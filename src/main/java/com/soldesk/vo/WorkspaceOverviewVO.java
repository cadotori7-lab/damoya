package com.soldesk.vo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 워크스페이스 개요 화면에 필요한 데이터를 한 번에 전달하는 조회 모델. */
public class WorkspaceOverviewVO {

    private static final DateTimeFormatter DATE_DISPLAY =
            DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private ProjectVO project;
    private int teamMemberCount;
    private List<String> tags = new ArrayList<>();
    private List<MeetingVO> nearestMeetings = new ArrayList<>();
    private List<MemberTask> memberTasks = new ArrayList<>();
    private TaskStats taskStats = new TaskStats();

    public ProjectVO getProject() {
        return project;
    }

    public void setProject(ProjectVO project) {
        this.project = project;
    }

    public int getTeamMemberCount() {
        return teamMemberCount;
    }

    public void setTeamMemberCount(int teamMemberCount) {
        this.teamMemberCount = teamMemberCount;
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
    }

    public List<MeetingVO> getNearestMeetings() {
        return Collections.unmodifiableList(nearestMeetings);
    }

    public void setNearestMeetings(List<MeetingVO> nearestMeetings) {
        this.nearestMeetings = nearestMeetings == null
                ? new ArrayList<>()
                : new ArrayList<>(nearestMeetings);
    }

    public List<MemberTask> getMemberTasks() {
        return Collections.unmodifiableList(memberTasks);
    }

    public void setMemberTasks(List<MemberTask> memberTasks) {
        this.memberTasks = memberTasks == null
                ? new ArrayList<>()
                : new ArrayList<>(memberTasks);
    }

    public TaskStats getTaskStats() {
        return taskStats;
    }

    public void setTaskStats(TaskStats taskStats) {
        this.taskStats = taskStats == null ? new TaskStats() : taskStats;
    }

    public boolean isRecruiting() {
        String status = project == null ? null : project.getStatus();
        return "RECRUITING".equalsIgnoreCase(status)
                || "RECRULTING".equalsIgnoreCase(status);
    }

    public String getProjectStatusLabel() {
        String status = project == null ? null : project.getStatus();
        if ("DONE".equalsIgnoreCase(status)) {
            return "프로젝트 종료";
        }
        if (isRecruiting()) {
            return "모집 중";
        }
        if ("CLOSED".equalsIgnoreCase(status)) {
            return "진행 중";
        }
        return "진행 중";
    }

    public String getProjectStatusClass() {
        String status = project == null ? null : project.getStatus();
        if ("DONE".equalsIgnoreCase(status)) {
            return "done";
        }
        return isRecruiting() ? "recruit" : "ing";
    }

    public String getTeamCountLabel() {
        if (isRecruiting() && project != null && project.getCapacity() != null) {
            return teamMemberCount + " / " + project.getCapacity() + "명";
        }
        return teamMemberCount + "명";
    }

    public String getStartDateLabel() {
        return formatProjectDate(project == null ? null : project.getStartDate());
    }

    public String getEndDateLabel() {
        return formatProjectDate(project == null ? null : project.getEndDate());
    }

    private String formatProjectDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        try {
            return LocalDate.parse(value.trim()).format(DATE_DISPLAY);
        } catch (DateTimeParseException ignored) {
            return value;
        }
    }

    public static class TaskStats {
        private int total;
        private int ongoing;
        private int review;
        private int rejected;
        private int approved;

        public int getTotal() {
            return total;
        }

        public int getOngoing() {
            return ongoing;
        }

        public int getReview() {
            return review;
        }

        public int getRejected() {
            return rejected;
        }

        public int getApproved() {
            return approved;
        }

        public void add(String status) {
            total++;
            if ("ONGOING".equalsIgnoreCase(status)) {
                ongoing++;
            } else if ("REVIEW".equalsIgnoreCase(status)) {
                review++;
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                rejected++;
            } else if ("APPROVED".equalsIgnoreCase(status)) {
                approved++;
            }
        }
    }

    public static class MemberTask {
        private static final DateTimeFormatter DUE_DISPLAY =
                DateTimeFormatter.ofPattern("MM.dd");

        private ParticipationVO member;
        private TaskVO nearestTask;

        public MemberTask(ParticipationVO member, TaskVO nearestTask) {
            this.member = member;
            this.nearestTask = nearestTask;
        }

        public ParticipationVO getMember() {
            return member;
        }

        public TaskVO getNearestTask() {
            return nearestTask;
        }

        public String getMemberInitial() {
            String name = member == null ? null : member.getMemberName();
            return name == null || name.trim().isEmpty()
                    ? "?"
                    : name.trim().substring(0, 1);
        }

        public String getRoleLabel() {
            return member != null
                    && "LEADER".equalsIgnoreCase(member.getProjectRole())
                    ? "팀장"
                    : "팀원";
        }

        public String getTaskStatusLabel() {
            if (nearestTask == null) {
                return "업무 없음";
            }
            String status = nearestTask.getStatus();
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

        public String getTaskStatusClass() {
            if (nearestTask == null) {
                return "done";
            }
            String status = nearestTask.getStatus();
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

        public String getDueDateLabel() {
            return nearestTask == null || nearestTask.getDue_date() == null
                    ? "-"
                    : nearestTask.getDue_date().format(DUE_DISPLAY);
        }

        public boolean isOverdue() {
            return nearestTask != null
                    && nearestTask.getDue_date() != null
                    && nearestTask.getDue_date().isBefore(LocalDate.now())
                    && !"APPROVED".equalsIgnoreCase(nearestTask.getStatus());
        }
    }
}
