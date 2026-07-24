package com.soldesk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.TaskVO;

public interface TaskMapper {

    // 프로젝트별 업무 목록
    List<TaskVO> selectTaskList(
            @Param("project_id") long project_id
    );

    // 업무 상세
    TaskVO selectTaskById(
            @Param("project_id") long project_id,
            @Param("task_id") long task_id
    );

    // 업무 등록
    int insertTask(TaskVO task);

    // 업무 수정
    int updateTask(TaskVO task);

    // 업무 삭제
    int deleteTask(
            @Param("project_id") long project_id,
            @Param("task_id") long task_id
    );
}