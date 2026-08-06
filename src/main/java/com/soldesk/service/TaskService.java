package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.TaskMapper;
import com.soldesk.vo.ProjectVO;
import com.soldesk.vo.TaskVO;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectService projectService;

    // 업무 등록
    @Transactional
    public int insertTask(long project_id, TaskVO task) {
        // URL의 프로젝트 ID를 사용
        task.setProject_id(project_id);

        int result = taskMapper.insertTask(task);
        if (result == 1 && task.getAssignee_id() != null) {
            notificationService.toMessage(project_id, task.getAssignee_id().intValue(), "TASK", task.getTask_name());
        }

        return result;
    }

    // 프로젝트별 업무 목록 조회
    @Transactional(readOnly = true)
    public List<TaskVO> selectTaskList(long project_id) {
        return taskMapper.selectTaskList(project_id);
    }

    // 업무 상세 조회
    @Transactional(readOnly = true)
    public TaskVO selectTaskById(
            long project_id,
            long task_id) {

        return taskMapper.selectTaskById(
                project_id,
                task_id
        );
    }

    // 업무 수정
    @Transactional
    public int updateTask(
            long project_id,
            long task_id,
            TaskVO task) {

        // URL의 값을 강제로 설정
        task.setProject_id(project_id);
        task.setTask_id(task_id);

        return taskMapper.updateTask(task);
    }

    // 업무 삭제
    @Transactional
    public int deleteTask(
            long project_id,
            long task_id) {

        return taskMapper.deleteTask(
                project_id,
                task_id
        );
    }

    //업무 제출
    @Transactional
    public boolean submitTask(TaskVO task) {
        boolean success = taskMapper.submitTask(task) == 1;
        if (success) {
            ProjectVO project = projectService.getProjectById(task.getProject_id());
            if (project != null) {
                TaskVO savedTask = taskMapper.selectTaskById(task.getProject_id(), task.getTask_id());
                notificationService.toMessage(
                        task.getProject_id(),
                        project.getOwnerId().intValue(),
                        "TASK",
                        "업무 제출- " + savedTask.getTask_name());
            }
        }
        return success;
    }

    //승인
    @Transactional
    public boolean approveTask(
            long project_id,
            long task_id) {
        TaskVO task = taskMapper.selectTaskById(project_id, task_id);
        boolean success = taskMapper.approveTask(
            project_id,
            task_id
        ) == 1;
        if (success && task != null && task.getAssignee_id() != null) {
            notificationService.toMessage(
                    project_id,
                    task.getAssignee_id().intValue(),
                    "TASK",
                    "업무 승인- " + task.getTask_name());
        }
        return success;
    }
    //반려
    @Transactional
    public boolean rejectTask(
            long project_id,
            long task_id,
            String reject_reason) {
        TaskVO task = taskMapper.selectTaskById(project_id, task_id);
        boolean success = taskMapper.rejectTask(
            project_id,
            task_id,
            reject_reason
        ) == 1;
        if (success && task != null && task.getAssignee_id() != null) {
            notificationService.toMessage(
                    project_id,
                    task.getAssignee_id().intValue(),
                    "TASK",
                    "업무 반려- " + task.getTask_name() + " (사유: " + reject_reason + ")");
        }
        return success;
    }

    // 담당자가 반려 사유를 확인하고 업무를 다시 진행
    @Transactional
    public boolean acknowledgeRejectedTask(
            long project_id,
            long task_id,
            long assignee_id) {

        return taskMapper.acknowledgeRejectedTask(
            project_id,
            task_id,
            assignee_id
        ) == 1;
    }
    @Transactional(readOnly = true)
    public List<TaskVO> selectTasksByAssignee(long assignee_id) {
        return taskMapper.selectTasksByAssignee(assignee_id);
    }
}
