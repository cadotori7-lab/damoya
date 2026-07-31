package com.soldesk.vo;

import java.util.List;

public class AdminDashboardStats {

    private final long memberCount;//회원 수
    private final long ongoingProjectCount;//진행 중 프로젝트 수
    private final List<MemberVO> approvedRequiredMembers;//승인이 필요한 회원 리스트
    private final int approvedRequiredMemberCount;//승인이 필요한 회원 수
    private final List<ReportVO> reportList;//신고 리스트

    public AdminDashboardStats(long memberCount, 
                               long ongoingProjectCount, 
                               List<MemberVO> approvedRequiredMembers, 
                               int approvedRequiredMemberCount,
                               List<ReportVO> reportList) {

        this.memberCount = memberCount;
        this.ongoingProjectCount = ongoingProjectCount;
        this.approvedRequiredMembers = approvedRequiredMembers;
        this.approvedRequiredMemberCount = approvedRequiredMemberCount;
        this.reportList = reportList;
    }

    public long getMemberCount() {
        return memberCount;
    }

    public long getOngoingProjectCount() {
        return ongoingProjectCount;
    }

    public List<MemberVO> getApprovedRequiredMembers() {
        return approvedRequiredMembers;
    }

    public int getApprovedRequiredMemberCount() {
        return approvedRequiredMemberCount;
    }
    public List<ReportVO> getReportList() {
        return reportList;
    }
}
