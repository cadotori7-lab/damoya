package com.soldesk.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.vo.MeetingVO;
import com.soldesk.vo.ParticipationVO;
import com.soldesk.vo.ProjectVO;
import com.soldesk.vo.TaskVO;
import com.soldesk.vo.WorkspaceOverviewVO;
import com.soldesk.vo.WorkspaceOverviewVO.MemberTask;
import com.soldesk.vo.WorkspaceOverviewVO.TaskStats;

@Service
public class WorkspaceOverviewService {

    private static final int OVERVIEW_MEETING_LIMIT = 3;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MeetingService meetingService;

    @Transactional(readOnly = true)
    public WorkspaceOverviewVO getOverview(long projectId) {
        ProjectVO project = projectService.getProjectById(projectId);
        if (project == null) {
            return null;
        }

        List<ParticipationVO> teamMembers =
                participationService.selectTaskMembers(projectId);
        List<TaskVO> tasks = taskService.selectTaskList(projectId);

        WorkspaceOverviewVO overview = new WorkspaceOverviewVO();
        overview.setProject(project);
        overview.setTeamMemberCount(teamMembers.size());
        overview.setTags(parseTags(project.getTags()));
        overview.setNearestMeetings(nearestMeetings(projectId));
        overview.setTaskStats(buildTaskStats(tasks));
        overview.setMemberTasks(buildMemberTasks(teamMembers, tasks));
        return overview;
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return java.util.Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<MeetingVO> nearestMeetings(long projectId) {
        LocalDateTime now = LocalDateTime.now();
        return meetingService.selectMeetingsByProjectId(projectId).stream()
                .filter(meeting -> meeting.getMeet_date() != null)
                .sorted(Comparator
                        .comparingLong((MeetingVO meeting) -> Math.abs(
                                Duration.between(
                                        now, meeting.getMeet_date()).toSeconds()))
                        .thenComparing(MeetingVO::getMeet_date))
                .limit(OVERVIEW_MEETING_LIMIT)
                .collect(Collectors.toList());
    }

    private TaskStats buildTaskStats(List<TaskVO> tasks) {
        TaskStats stats = new TaskStats();
        for (TaskVO task : tasks) {
            stats.add(task.getStatus());
        }
        return stats;
    }

    private List<MemberTask> buildMemberTasks(
            List<ParticipationVO> teamMembers,
            List<TaskVO> tasks) {

        Map<Long, List<TaskVO>> tasksByAssignee = tasks.stream()
                .filter(task -> task.getAssignee_id() != null)
                .collect(Collectors.groupingBy(TaskVO::getAssignee_id));

        Map<Long, ParticipationVO> memberById = teamMembers.stream()
                .collect(Collectors.toMap(
                        ParticipationVO::getMemberId,
                        Function.identity(),
                        (first, ignored) -> first));

        Comparator<TaskVO> closestDueDate = Comparator
                .comparingLong(this::daysFromToday)
                .thenComparing(
                        TaskVO::getDue_date,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(
                        TaskVO::getTask_id,
                        Comparator.nullsLast(Comparator.naturalOrder()));

        List<MemberTask> memberTasks = new ArrayList<>();
        for (ParticipationVO member : teamMembers) {
            List<TaskVO> assigned = tasksByAssignee.getOrDefault(
                    member.getMemberId(), new ArrayList<>());

            List<TaskVO> active = assigned.stream()
                    .filter(task -> !"APPROVED".equalsIgnoreCase(task.getStatus()))
                    .collect(Collectors.toList());

            List<TaskVO> candidates = active.isEmpty() ? assigned : active;
            TaskVO nearest = candidates.stream()
                    .min(closestDueDate)
                    .orElse(null);

            memberTasks.add(new MemberTask(
                    memberById.get(member.getMemberId()), nearest));
        }
        return memberTasks;
    }

    private long daysFromToday(TaskVO task) {
        LocalDate dueDate = task.getDue_date();
        return dueDate == null
                ? Long.MAX_VALUE
                : Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), dueDate));
    }
}
