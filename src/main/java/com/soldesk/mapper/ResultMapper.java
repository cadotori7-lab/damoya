package com.soldesk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.ResultVO;

public interface ResultMapper {

    List<ResultVO> selectApprovedResults(
            @Param("projectId") long projectId);
}
