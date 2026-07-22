package com.soldesk.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.vo.MemberDocument;
import com.soldesk.vo.MemberVO;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;

@Service
public class ElasticSearchService implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchService.class);

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired(required = false)
    private MemberMapper memberMapper;

    @Value("${elasticsearch.index.members:${ELASTICSEARCH_MEMBERS_INDEX:damoya-members}}")
    private String membersIndex;

    @Value("${elasticsearch.index.projects:${ELASTICSEARCH_PROJECTS_INDEX:damoya-projects}}")
    private String projectsIndex;

    @Value("${elasticsearch.index.reports:${ELASTICSEARCH_REPORTS_INDEX:damoya-reports}}")
    private String reportsIndex;

    @Override
    public void afterPropertiesSet() {
        try {
            createIndexIfNotExists();
            createSimpleIndexIfNotExists(projectsIndex);
            createSimpleIndexIfNotExists(reportsIndex);
            reindexAll();
        } catch (Exception e) {
            log.warn("Elasticsearch 초기화 실패 - 관리자 통계가 제한될 수 있습니다.", e);
        }
    }

    // 회원 인덱싱
    public void indexMember(MemberVO member) {
        try {
            createIndexIfNotExists();
            elasticsearchClient.index(index -> index
                .index(membersIndex)
                .id(String.valueOf(member.getMember_id()))
                .document(MemberDocument.fromMemberVO(member)));

            log.debug("회원 인덱싱 완료: memberId={}", member.getMember_id());
        } catch (Exception e) {
            log.warn("회원 인덱싱 실패: memberId={}", member.getMember_id(), e);
        }
    }

    // 회원 인덱스 삭제
    public void deleteMember(int memberId) {
        try {
            if (!indexExists(membersIndex)) {
                return;
            }

            elasticsearchClient.delete(delete -> delete
                .index(membersIndex)
                .id(String.valueOf(memberId)));

            log.debug("회원 인덱스 삭제 완료: memberId={}", memberId);
        } catch (Exception e) {
            log.warn("회원 인덱스 삭제 실패: memberId={}", memberId, e);
        }
    }

    // 인덱스 준비 (회원 인덱스 없으면 생성 + 재색인, 부가 인덱스도 확인)
    public void ensureIndicesReady() throws IOException {
        if (!indexExists(membersIndex)) {
            createIndexIfNotExists();
            reindexAll();
        }
        createSimpleIndexIfNotExists(projectsIndex);
        createSimpleIndexIfNotExists(reportsIndex);
    }

    // ===== 아래부터는 인덱스 이름을 파라미터로 받는 범용 기능 (다른 서비스에서도 재사용 가능) =====

    // 인덱스 존재 여부 확인
    public boolean indexExists(String indexName) throws IOException {
        return elasticsearchClient.indices()
            .exists(ExistsRequest.of(request -> request.index(indexName)))
            .value();
    }

    // 매핑 없는 단순 인덱스 생성 (없을 때만)
    public void createSimpleIndexIfNotExists(String indexName) throws IOException {
        if (indexExists(indexName)) {
            return;
        }

        elasticsearchClient.indices().create(CreateIndexRequest.of(create -> create.index(indexName)));
        log.info("Elasticsearch 인덱스 생성 완료: {}", indexName);
    }

    // 인덱스 문서 수 조회
    public long countDocuments(String indexName) throws IOException {
        if (!indexExists(indexName)) {
            return 0L;
        }
        return elasticsearchClient.count(count -> count.index(indexName)).count();
    }

    // 클러스터 상태 조회
    public String getClusterStatus() throws IOException {
        return elasticsearchClient.cluster()
            .health()
            .status()
            .jsonValue();
    }

    // ===== 회원 인덱스 전용 내부 로직 =====

    // 회원 인덱스 생성
    private void createIndexIfNotExists() throws IOException {
        if (indexExists(membersIndex)) {
            putMemberMapping();
            return;
        }

        elasticsearchClient.indices().create(CreateIndexRequest.of(create -> create
            .index(membersIndex)
            .mappings(mappings -> mappings
                .properties("member_id", property -> property.integer(integer -> integer))
                .properties("login_id", property -> property.keyword(keyword -> keyword))
                .properties("email", property -> property.keyword(keyword -> keyword))
                .properties("name", property -> property.text(text -> text))
                .properties("dept_id", property -> property.long_(longNumber -> longNumber))
                .properties("grade", property -> property.integer(integer -> integer))
                .properties("major", property -> property.keyword(keyword -> keyword))
                .properties("double_major", property -> property.keyword(keyword -> keyword))
                .properties("intro", property -> property.text(text -> text))
                .properties("role", property -> property.keyword(keyword -> keyword))
                .properties("account_status", property -> property.keyword(keyword -> keyword))
                .properties("approved", property -> property.boolean_(bool -> bool))
                .properties("profile_public", property -> property.boolean_(bool -> bool)))));

        log.info("Elasticsearch 회원 인덱스 생성 완료: {}", membersIndex);
    }

    // 회원 인덱스 매핑
    private void putMemberMapping() throws IOException {
        elasticsearchClient.indices().putMapping(mapping -> mapping
            .index(membersIndex)
            .properties("member_id", property -> property.integer(integer -> integer))
            .properties("login_id", property -> property.keyword(keyword -> keyword))
            .properties("email", property -> property.keyword(keyword -> keyword))
            .properties("name", property -> property.text(text -> text))
            .properties("dept_id", property -> property.long_(longNumber -> longNumber))
            .properties("grade", property -> property.integer(integer -> integer))
            .properties("major", property -> property.keyword(keyword -> keyword))
            .properties("double_major", property -> property.keyword(keyword -> keyword))
            .properties("intro", property -> property.text(text -> text))
            .properties("role", property -> property.keyword(keyword -> keyword))
            .properties("account_status", property -> property.keyword(keyword -> keyword))
            .properties("approved", property -> property.boolean_(bool -> bool))
            .properties("profile_public", property -> property.boolean_(bool -> bool)));
    }

    private void reindexAll() throws IOException {
        if (memberMapper == null) {
            log.warn("MemberMapper가 등록되지 않아 회원 전체 재색인을 건너뜁니다.");
            return;
        }

        List<MemberVO> members = memberMapper.findAllMembers();
        for (MemberVO member : members) {
            elasticsearchClient.index(index -> index
                .index(membersIndex)
                .id(String.valueOf(member.getMember_id()))
                .document(MemberDocument.fromMemberVO(member)));
        }
        elasticsearchClient.indices().refresh(refresh -> refresh.index(membersIndex));

        log.info("Elasticsearch 회원 reindex 완료: {}건", members.size());
    }
}