package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.mapper.ProjectMapper;
import com.soldesk.mapper.ReportMapper;
import com.soldesk.vo.AdminDashboardStats;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.ReportVO;

@Service
public class AdminDashboardService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ReportMapper reportMapper;

    // 대시보드 통계 조회 (전체 회원 수, 진행 중 프로젝트 수)
    public AdminDashboardStats getDashboardStats() {
        long memberCount = memberMapper.countAllMembers();
        long ongoingProjectCount = projectMapper.countByStatus("RECRUITING");
        List<MemberVO> approvedRequiredMembers = memberMapper.getApprovedRequiredMembers();
        int approvedRequiredMemberCount = memberMapper.countApprovedRequiredMembers();
        List<ReportVO> reportList = reportMapper.getReportList();
        return new AdminDashboardStats(memberCount, 
                                    ongoingProjectCount, 
                                    approvedRequiredMembers, 
                                    approvedRequiredMemberCount,
                                    reportList);
    }
}
