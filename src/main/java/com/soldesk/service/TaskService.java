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
    public void insertTask(TaskVO task) {
        taskMapper.insertTask(task);
    }

    // 프로젝트별 업무 목록 조회
    @Transactional(readOnly = true)
    public List<TaskVO> selectTaskList(long project_id) {
        return taskMapper.selectTaskList(project_id);
    }

    // 업무 상세 조회
    @Transactional(readOnly = true)
    public TaskVO selectTaskById(long task_id) {
        return taskMapper.selectTaskById(task_id);
    }

    // 업무 수정
    @Transactional
    public void updateTask(TaskVO task) {
        taskMapper.updateTask(task);
    }

    // 업무 삭제
    @Transactional
    public void deleteTask(long task_id) {
        taskMapper.deleteTask(task_id);
    }
}
