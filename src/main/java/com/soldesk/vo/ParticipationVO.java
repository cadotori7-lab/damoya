package com.soldesk.vo;

import java.time.LocalDateTime;

public class ParticipationVO {
    private Long partId; //참여pk

    private Long projectId; //프로젝트pk

    private Long memberId; //사용자pk

    private String projectRole; //프로젝트 역할

    private String joinStatus; //참여여부(대기/면접/참여)

    private Integer successionOrder; //승계우선순위(팀원)

    private Integer contribution; //기여도(완료 업무 수 등)

    private LocalDateTime appliedAt; //지원&합루 시각

    //getter,setter
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

    public Integer getSuccessionOrder() {
        return successionOrder;
    }

    public void setSuccessionOrder(Integer successionOrder) {
        this.successionOrder = successionOrder;
    }

    public Integer getContribution() {
        return contribution;
    }

    public void setContribution(Integer contribution) {
        this.contribution = contribution;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}
