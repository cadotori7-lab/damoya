package com.soldesk.vo;

public class MemberDocument {

    private int member_id; // 회원 고유번호
    private String login_id; // 로그인 아이디
    private String email; // 이메일
    private String name; // 이름
    private int dept_id; // 학과 고유번호
    private int grade; // 학년
    private String major; // 전공
    private String double_major; // 복수전공
    private String intro; // 자기소개
    private String role; // 권한
    private String account_status; // 계정 상태
    private boolean approved; // 승인 여부(대학 관련)
    private boolean profile_public; // 프로필 공개 여부

    // MemberVO를 MemberDocument로 변환
    public static MemberDocument fromMemberVO(MemberVO memberVO) {
        MemberDocument document = new MemberDocument();
        document.setMember_id(memberVO.getMember_id());
        document.setLogin_id(memberVO.getLogin_id());
        document.setEmail(memberVO.getEmail());
        document.setName(memberVO.getName());
        document.setDept_id(memberVO.getDept_id());
        document.setGrade(memberVO.getGrade());
        document.setMajor(memberVO.getMajor());
        document.setDouble_major(memberVO.getDouble_major());
        document.setIntro(memberVO.getIntro());
        document.setRole(memberVO.getRole());
        document.setAccount_status(memberVO.getAccount_status());
        document.setApproved(memberVO.isApproved());
        document.setProfile_public(memberVO.isProfile_public());
        return document;
    }

    // MemberDocument를 MemberVO로 변환
    public MemberVO toMemberVO() {
        MemberVO memberVO = new MemberVO();
        memberVO.setMember_id(getMember_id());
        memberVO.setLogin_id(getLogin_id());
        memberVO.setEmail(getEmail());
        memberVO.setName(getName());
        memberVO.setDept_id(getDept_id());
        memberVO.setGrade(getGrade());
        memberVO.setMajor(getMajor());
        memberVO.setDouble_major(getDouble_major());
        memberVO.setIntro(getIntro());
        memberVO.setRole(getRole());
        memberVO.setAccount_status(getAccount_status());
        memberVO.setApproved(isApproved());
        memberVO.setProfile_public(isProfile_public());
        return memberVO;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDept_id() {
        return dept_id;
    }

    public void setDept_id(int dept_id) {
        this.dept_id = dept_id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDouble_major() {
        return double_major;
    }

    public void setDouble_major(String double_major) {
        this.double_major = double_major;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
    
    
}
