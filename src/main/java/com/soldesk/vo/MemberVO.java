package com.soldesk.vo;

public class MemberVO {
   private int member_id; // 회원 고유번호
   private String login_id; // 로그인 아이디
   private String password; // 로그인 비밀번호
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

   private String passwordConfirm; // 비밀번호 확인

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
   public String getPassword() {
    return password;
   }
   public void setPassword(String password) {
    this.password = password;
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
   public String getPasswordConfirm() {
    return passwordConfirm;
   }
   public void setPasswordConfirm(String passwordConfirm) {
    this.passwordConfirm = passwordConfirm;
   }

}