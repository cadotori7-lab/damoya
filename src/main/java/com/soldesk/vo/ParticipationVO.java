package com.soldesk.vo;

import java.time.LocalDateTime;

public class ParticipationVO {
    private Long part_id; //참여pk

    private Long project_id; //프로젝트pk

    private Long member_id; //사용자pk

    private String project_role; //프로젝트 역할

    private String join_status; //참여여부(대기/면접/참여)

    private Integer succession_order; //승계우선순위(팀원)

    private Integer contribution; //기여도(완료 업무 수 등)

    private LocalDateTime applied_at; //지원&합류 시각

    //get/set
    public Long getPart_id() {
        return part_id;
    }

    public void setPart_id(Long part_id) {
        this.part_id = part_id;
    }

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public String getProject_role() {
        return project_role;
    }

    public void setProject_role(String project_role) {
        this.project_role = project_role;
    }

    public String getJoin_status() {
        return join_status;
    }

    public void setJoin_status(String join_status) {
        this.join_status = join_status;
    }

    public Integer getSuccession_order() {
        return succession_order;
    }

    public void setSuccession_order(Integer succession_order) {
        this.succession_order = succession_order;
    }

    public Integer getContribution() {
        return contribution;
    }

    public void setContribution(Integer contribution) {
        this.contribution = contribution;
    }

    public LocalDateTime getApplied_at() {
        return applied_at;
    }

    public void setApplied_at(LocalDateTime applied_at) {
        this.applied_at = applied_at;
    }

}
