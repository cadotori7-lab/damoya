package com.soldesk.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.vo.MemberDocument;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorDocument;

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

    // 회원 인덱스
    @Value("${elasticsearch.index.members:${ELASTICSEARCH_MEMBERS_INDEX:damoya-members}}")
    private String membersIndex;

    // 멘토 인덱스
    @Value("${elasticsearch.index.mentors:${ELASTICSEARCH_MENTORS_INDEX:damoya-mentors}}")
    private String mentorsIndex;

    // 인덱스 초기화
    @Override
    public void afterPropertiesSet() {
        initializeMemberIndex();
        initializeMentorIndex();
    }

    // 회원 인덱스 초기화
    private void initializeMemberIndex() {
        try {
            createIndexIfNotExists();
            reindexAll();
        } catch (Exception e) {
            log.warn("Elasticsearch 회원 인덱스 초기화 실패", e);
        }
    }

    // 멘토 인덱스 초기화
    private void initializeMentorIndex() {
        try {
            createMentorIndexIfNotExists();
            reindexMentors();
        } catch (Exception e) {
            log.warn("Elasticsearch 멘토 인덱스 초기화 실패", e);
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
        if (!indexExists(mentorsIndex)) {
            createMentorIndexIfNotExists();
            reindexMentors();
        }
    }

    // 인덱스 존재 여부 확인
    public boolean indexExists(String indexName) throws IOException {
        return elasticsearchClient.indices()
            .exists(ExistsRequest.of(request -> request.index(indexName)))
            .value();
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

    // 멘토 인덱스 생성
    private void createMentorIndexIfNotExists() throws IOException {
        if (indexExists(mentorsIndex)) {
            putMentorMapping();
            return;
        }

        elasticsearchClient.indices().create(CreateIndexRequest.of(create -> create
            .index(mentorsIndex)
            .mappings(mappings -> mappings
                .properties("member_id", property -> property.integer(integer -> integer))
                .properties("login_id", property -> property.keyword(keyword -> keyword))
                .properties("name", property -> property.text(text -> text))
                .properties("dept_id", property -> property.long_(longNumber -> longNumber))
                .properties("intro", property -> property.text(text -> text))
                .properties("account_status", property -> property.keyword(keyword -> keyword))
                .properties("approved", property -> property.boolean_(bool -> bool))
                .properties("profile_public", property -> property.boolean_(bool -> bool))
                .properties("field", property -> property.text(text -> text))
                .properties("career", property -> property.text(text -> text))
                .properties("cert", property -> property.text(text -> text))
                .properties("search_text", property -> property.text(text -> text))
                .properties("mentor_reference", property -> property.text(text -> text))
                .properties("embedding_source_hash", property -> property.keyword(keyword -> keyword))
                .properties("embedding_model", property -> property.keyword(keyword -> keyword))
                .properties("sync_batch_id", property -> property.keyword(keyword -> keyword))
                .properties("embedding", property -> property.denseVector(vector -> vector
                    .dims(384)
                    .index(true)
                    .similarity("cosine"))))));

        log.info("Elasticsearch 멘토 인덱스 생성 완료: {}", mentorsIndex);
    }

    // 멘토 인덱스 매핑
    private void putMentorMapping() throws IOException {
        elasticsearchClient.indices().putMapping(mapping -> mapping
            .index(mentorsIndex)
            .properties("member_id", property -> property.integer(integer -> integer))
            .properties("login_id", property -> property.keyword(keyword -> keyword))
            .properties("name", property -> property.text(text -> text))
            .properties("dept_id", property -> property.long_(longNumber -> longNumber))
            .properties("intro", property -> property.text(text -> text))
            .properties("account_status", property -> property.keyword(keyword -> keyword))
            .properties("approved", property -> property.boolean_(bool -> bool))
            .properties("profile_public", property -> property.boolean_(bool -> bool))
            .properties("field", property -> property.text(text -> text))
            .properties("career", property -> property.text(text -> text))
            .properties("cert", property -> property.text(text -> text))
            .properties("search_text", property -> property.text(text -> text))
            .properties("mentor_reference", property -> property.text(text -> text))
            .properties("embedding_source_hash", property -> property.keyword(keyword -> keyword))
            .properties("embedding_model", property -> property.keyword(keyword -> keyword))
            .properties("sync_batch_id", property -> property.keyword(keyword -> keyword))
            .properties("embedding", property -> property.denseVector(vector -> vector
                .dims(384)
                .index(true)
                .similarity("cosine"))));
    }

    // 회원 전체 재색인
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

    // 멘토 전체 재색인
    public void reindexMentors() throws IOException {
        if (memberMapper == null) {
            log.warn("MemberMapper가 등록되지 않아 멘토 전체 재색인을 건너뜁니다.");
            return;
        }
        // 멘토 인덱스 생성
        createMentorIndexIfNotExists();
        // 멘토 목록 조회
        List<MentorDocument> mentors = memberMapper.findAllMentors();
        String syncBatchId = UUID.randomUUID().toString();

        // 멘토 인덱스 업데이트
        for (MentorDocument mentor : mentors) {
            mentor.rebuildSearchText();
            mentor.setSync_batch_id(syncBatchId);
            elasticsearchClient.update(update -> update
                .index(mentorsIndex)
                .id(String.valueOf(mentor.getMember_id()))
                .doc(mentor)
                .docAsUpsert(true),
                MentorDocument.class);
        }
        // 기존 멘토 인덱스 삭제
        elasticsearchClient.deleteByQuery(delete -> delete
            .index(mentorsIndex)
            .query(query -> query.bool(bool -> bool
                .mustNot(not -> not.term(term -> term
                    .field("sync_batch_id")
                    .value(syncBatchId)))))
            .refresh(true));
        elasticsearchClient.indices().refresh(refresh -> refresh.index(mentorsIndex));

        log.info("Elasticsearch 멘토 reindex 완료: {}건", mentors.size());
    }
}
