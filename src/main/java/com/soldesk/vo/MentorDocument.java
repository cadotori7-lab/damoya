package com.soldesk.vo;

import java.util.ArrayList;
import java.util.List;

// Elasticsearch 멘토 인덱스에 저장하는 문서
public class MentorDocument {

    private int member_id; // 회원 고유번호
    private String login_id; // 로그인 아이디
    private String name; // 이름
    private Integer dept_id; // 학과 고유번호, 외부 멘토는 null 가능
    private String intro; // 자기소개
    private String account_status; // 계정 상태
    private boolean approved; // 승인 여부
    private boolean profile_public; // 프로필 공개 여부
    private String field; // 전문 분야
    private String career; // 경력
    private String cert; // 자격증
    private String search_text; // Elasticsearch 텍스트 검색용 정보
    private String mentor_reference; // 임베딩 및 LLM 전달용 한 줄 정보
    private String sync_batch_id; // 전체 동기화 시 현재 문서 식별값

    // MemberVO와 멘토 전용 정보를 MentorDocument로 변환
    public static MentorDocument fromMemberVO(
            MemberVO memberVO,
            String field,
            String career,
            String cert) {
        MentorDocument document = new MentorDocument();
        document.setMember_id(memberVO.getMember_id());
        document.setLogin_id(memberVO.getLogin_id());
        document.setName(memberVO.getName());
        document.setDept_id(memberVO.getDept_id());
        document.setIntro(memberVO.getIntro());
        document.setAccount_status(memberVO.getAccount_status());
        document.setApproved(memberVO.isApproved());
        document.setProfile_public(memberVO.isProfile_public());
        document.setField(field);
        document.setCareer(career);
        document.setCert(cert);
        document.rebuildSearchText();
        return document;
    }

    // MentorDocument의 회원 공통 정보를 MemberVO로 변환
    public MemberVO toMemberVO() {
        MemberVO memberVO = new MemberVO();
        memberVO.setMember_id(getMember_id());
        memberVO.setLogin_id(getLogin_id());
        memberVO.setName(getName());
        if (getDept_id() != null) {
            memberVO.setDept_id(getDept_id());
        }
        memberVO.setIntro(getIntro());
        memberVO.setAccount_status(getAccount_status());
        memberVO.setApproved(isApproved());
        memberVO.setProfile_public(isProfile_public());
        return memberVO;
    }

    // 검색과 임베딩에 사용할 멘토 소개 문자열 생성
    public void rebuildSearchText() {
        List<String> parts = new ArrayList<>();
        addPart(parts, "멘토 이름", name);
        addPart(parts, "전문 분야", field);
        addPart(parts, "소개", intro);
        addPart(parts, "경력", career);
        addPart(parts, "자격증", cert);
        search_text = String.join("\n", parts);
        mentor_reference = String.join(" | ", parts);
    }

    private static void addPart(List<String> parts, String label, String value) {
        if (value != null && !value.isBlank()) {
            parts.add(label + ": " + value.trim());
        }
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDept_id() {
        return dept_id;
    }

    public void setDept_id(Integer dept_id) {
        this.dept_id = dept_id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getAccount_status() {
        return account_status;
    }

    public void setAccount_status(String account_status) {
        this.account_status = account_status;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isProfile_public() {
        return profile_public;
    }

    public void setProfile_public(boolean profile_public) {
        this.profile_public = profile_public;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public String getSearch_text() {
        return search_text;
    }

    public void setSearch_text(String search_text) {
        this.search_text = search_text;
    }

    public String getMentor_reference() {
        return mentor_reference;
    }

    public void setMentor_reference(String mentor_reference) {
        this.mentor_reference = mentor_reference;
    }

    public String getSync_batch_id() {
        return sync_batch_id;
    }

    public void setSync_batch_id(String sync_batch_id) {
        this.sync_batch_id = sync_batch_id;
    }
}
