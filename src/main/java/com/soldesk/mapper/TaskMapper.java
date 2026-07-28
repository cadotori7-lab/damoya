package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.TaskVO;

public interface TaskMapper {

    // 프로젝트의 업무 목록
    List<TaskVO> selectTaskList(long project_id);

    // 업무 상세
    TaskVO selectTaskById(long task_id);

    // 업무 등록
    void insertTask(TaskVO task);

    // 업무 수정
    void updateTask(TaskVO task);

    // 업무 삭제
    void deleteTask(long task_id);

}