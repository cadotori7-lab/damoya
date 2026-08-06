package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.TaskMapper;
import com.soldesk.vo.TaskVO;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    // 업무 등록
    @Transactional
    public int insertTask(long project_id, TaskVO task) {

        // URL의 프로젝트 ID를 사용
        task.setProject_id(project_id);

        return taskMapper.insertTask(task);
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
        return taskMapper.submitTask(task) == 1;
    }

    //승인
    @Transactional
    public boolean approveTask(
            long project_id,
            long task_id) {

        return taskMapper.approveTask(
            project_id,
            task_id
        ) == 1;
    }
    //반려
    @Transactional
    public boolean rejectTask(
            long project_id,
            long task_id,
            String reject_reason) {

        return taskMapper.rejectTask(
            project_id,
            task_id,
            reject_reason
        ) == 1;
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
