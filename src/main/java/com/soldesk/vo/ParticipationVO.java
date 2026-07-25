package com.soldesk.vo;

import java.time.LocalDateTime;

public class ParticipationVO {
    private Long partId; //참여ID
    private Long projectId;
    private Long memberId;
    private String projectRole; //참여역할(LEADER,MEMBER.MENTOR)
    private String joinStatus; // 참여여부(대기/면접/참여)
    private int successionOrder; // 승계 우선순위(팀원)
    private LocalDateTime  appliedAt; //지원 
    private int contribution; // 기여도(완료 업무 수 등)
    private String wantPosition; //원하는 포지션
    private String experience; //경험/경력
    private String motive; //지원동기 
    private String memberName; // 지원자 이름


    public Long getPartId() {
        return partId;
    }
    public void setPartId(Long partId) {
        this.partId = partId;
    }
    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public Long getMemberId() {
        return memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    public String getProjectRole() {
        return projectRole;
    }
    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }
    public String getJoinStatus() {
        return joinStatus;
    }
    public void setJoinStatus(String joinStatus) {
        this.joinStatus = joinStatus;
    }
    public int getSuccessionOrder() {
        return successionOrder;
    }
    public void setSuccessionOrder(int successionOrder) {
        this.successionOrder = successionOrder;
    }
    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
    public int getContribution() {
        return contribution;
    }
    public void setContribution(int contribution) {
        this.contribution = contribution;
    }
    public String getWantPosition() {
        return wantPosition;
    }
    public void setWantPosition(String wantPosition) {
        this.wantPosition = wantPosition;
    }
    public String getExperience() {
        return experience;
    }
    public void setExperience(String experience) {
        this.experience = experience;
    }
    public String getMotive() {
        return motive;
    }
    public void setMotive(String motivate) {
        this.motive = motivate;
    }
    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
