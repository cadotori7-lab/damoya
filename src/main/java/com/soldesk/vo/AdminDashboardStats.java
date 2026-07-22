package com.soldesk.vo;

public class AdminDashboardStats {

    private final boolean connected;
    private final String clusterStatus;
    private final String errorMessage;
    private final long memberCount;
    private final long projectCount;
    private final long reportCount;

    private AdminDashboardStats(
            boolean connected,
            String clusterStatus,
            String errorMessage,
            long memberCount,
            long projectCount,
            long reportCount) {
        this.connected = connected;
        this.clusterStatus = clusterStatus;
        this.errorMessage = errorMessage;
        this.memberCount = memberCount;
        this.projectCount = projectCount;
        this.reportCount = reportCount;
    }

    public static AdminDashboardStats connected(
            String clusterStatus,
            long memberCount,
            long projectCount,
            long reportCount) {
        return new AdminDashboardStats(
                true, clusterStatus, null, memberCount, projectCount, reportCount);
    }

    public static AdminDashboardStats disconnected(String errorMessage) {
        return new AdminDashboardStats(false, "unavailable", errorMessage, 0L, 0L, 0L);
    }

    public boolean isConnected() {
        return connected;
    }

    public String getClusterStatus() {
        return clusterStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getMemberCount() {
        return memberCount;
    }

    public long getProjectCount() {
        return projectCount;
    }

    public long getReportCount() {
        return reportCount;
    }

    public long getTotalDocumentCount() {
        return memberCount + projectCount + reportCount;
    }
}
