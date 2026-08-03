package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.ReportMapper;
import com.soldesk.vo.ReportVO;

@Service
public class ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Transactional
    public void addReport(ReportVO reportVO) {
        reportMapper.addReport(reportVO);
    }
    @Transactional
    public List<ReportVO> getReportsByTarget(String targetType, Long targetId) {
        return reportMapper.getReportsByTarget(targetType, targetId);
    }
    @Transactional
    public void deleteReport(String targetType, Long targetId) {
        reportMapper.deleteReport(targetType, targetId);
    }

    @Transactional(readOnly = true)
    public List<ReportVO> getReportsByTargetType(String targetType) {
        return reportMapper.getReportsByTargetType(targetType);
    }

    @Transactional
    public void updateReportStatusByTarget(String targetType, Long targetId, String status) {
        reportMapper.updateReportStatusByTarget(targetType, targetId, status);
    }

}
