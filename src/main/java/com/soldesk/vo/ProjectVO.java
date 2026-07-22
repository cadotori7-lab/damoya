package com.soldesk.vo;

import java.time.LocalDateTime;

public class ProjectVO {
    private Long projectId;      // 프로젝트 id
    private Long ownerId;        // 개설자(초기 팀장) FK
    private String title;        // 제목
    private String category;     // 카테고리
    private String summary;      // 소개
    private Integer capacity;    // 모집 인원수
    private String targetGrade;  // 대상 학년 
    private String startDate; // 시작일
    private String endDate;   // 종료일
    private String status;     //모집 상태  
    private boolean isPublic;    // 공개 여부 
    private LocalDateTime createdAt; // 생성일시
    private String matchScope; //매칭범위
    private String tags; //태그

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getMatchScope() {
        return matchScope;
    }
    public void setMatchScope(String matchScope) {
        this.matchScope = matchScope;
    }
    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public Long getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Long memberId) {
        this.ownerId = memberId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public Integer getCapacity() {
        return capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    public String getTargetGrade() {
        return targetGrade;
    }
    public void setTargetGrade(String targetGrade) {
        this.targetGrade = targetGrade;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        if (endDate == null || endDate.trim().isEmpty()) {
            this.endDate = null; // 빈 값이면 null로 세팅하여 DB 에러 방지
        } else {
            this.endDate = endDate;
        }
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public boolean isPublic() {
        return isPublic;
    }
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    public LocalDateTime getReatedAt() {
        return createdAt;
    }
    public void setReatedAt(LocalDateTime reatedAt) {
        this.createdAt = reatedAt;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
}
