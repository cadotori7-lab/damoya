package com.soldesk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.soldesk.vo.AdminDashboardStats;

@Service
public class AdminDashboardService {

    private static final Logger log = LoggerFactory.getLogger(AdminDashboardService.class);

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Value("${elasticsearch.index.members:${ELASTICSEARCH_MEMBERS_INDEX:damoya-members}}")
    private String membersIndex;

    @Value("${elasticsearch.index.projects:${ELASTICSEARCH_PROJECTS_INDEX:damoya-projects}}")
    private String projectsIndex;

    @Value("${elasticsearch.index.reports:${ELASTICSEARCH_REPORTS_INDEX:damoya-reports}}")
    private String reportsIndex;

    // 대시보드 통계 조회
    public AdminDashboardStats getDashboardStats() {
        try {
            elasticSearchService.ensureIndicesReady();

            String clusterStatus = elasticSearchService.getClusterStatus();
            long memberCount = elasticSearchService.countDocuments(membersIndex);
            long projectCount = elasticSearchService.countDocuments(projectsIndex);
            long reportCount = elasticSearchService.countDocuments(reportsIndex);

            return AdminDashboardStats.connected(
                clusterStatus, memberCount, projectCount, reportCount);
        } catch (Exception e) {
            log.warn("Elasticsearch 대시보드 집계 실패: {}", e.getMessage());
            return AdminDashboardStats.disconnected(
                "Elasticsearch에 연결할 수 없습니다. 서버와 연결 설정을 확인하세요.");
        }
    }
}
