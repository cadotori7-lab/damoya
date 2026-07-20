package com.soldesk.vo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminDashboardStats {

    private final boolean connected;
    private final String clusterStatus;
    private final String errorMessage;
    private final Map<String, Long> indexDocumentCounts;

    private AdminDashboardStats(
            boolean connected,
            String clusterStatus,
            String errorMessage,
            Map<String, Long> indexDocumentCounts) {
        this.connected = connected;
        this.clusterStatus = clusterStatus;
        this.errorMessage = errorMessage;
        this.indexDocumentCounts = Collections.unmodifiableMap(
            new LinkedHashMap<>(indexDocumentCounts)
        );
    }

    public static AdminDashboardStats connected(
            String clusterStatus,
            Map<String, Long> indexDocumentCounts) {
        return new AdminDashboardStats(true, clusterStatus, null, indexDocumentCounts);
    }

    public static AdminDashboardStats disconnected(
            String errorMessage,
            Map<String, Long> indexDocumentCounts) {
        return new AdminDashboardStats(false, "unavailable", errorMessage, indexDocumentCounts);
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

    public Map<String, Long> getIndexDocumentCounts() {
        return indexDocumentCounts;
    }

    public long getMemberCount() {
        return countAt(0);
    }

    public long getProjectCount() {
        return countAt(1);
    }

    public long getReportCount() {
        return countAt(2);
    }

    public long getTotalDocumentCount() {
        return indexDocumentCounts.values().stream().mapToLong(Long::longValue).sum();
    }

    private long countAt(int position) {
        return indexDocumentCounts.values().stream()
            .skip(position)
            .findFirst()
            .orElse(0L);
    }
}
