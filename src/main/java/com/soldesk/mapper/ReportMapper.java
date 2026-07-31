package com.soldesk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.ReportVO;

public interface ReportMapper {

    public void addReport(ReportVO reportVO);
    public List<ReportVO> getReportsByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);
    public void deleteReport(@Param("targetType") String targetType, @Param("targetId") Long targetId);
    public List<ReportVO> getReportList();

}
