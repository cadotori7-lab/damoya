package com.soldesk.vo;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime createdAt; // 생성일시
    private String matchScope; //매칭범위
    private String tags; //태그
    private int viewCount; // 게시물 조회 수
    private int favoriteCount; // 관심 등록 수 
    private int page = 1;            // 현재 페이지 (기본값 1)
    private int amount = 6;          // 페이지당 보여줄 개수 (기본값 6)
    private int offset;              // DB 쿼리용 시작 위치
    private String keyword;          // 검색어
    private String tab;              // 상단 탭 상태 (all, RECRUITING, CLOSED)
    private String sort;             // 정렬 기준
    private List<String> categoryList; // 다중 카테고리 체크박스 값
    private List<String> gradeList;    // 다중 학년 체크박스 값
    private boolean liked; // 현재 로그인한 유저가 좋아요를 눌렀는지 여부
    private int commentCount; //댓글 수 
    private int currentNum; //현재 참여 중인 인원수
    private String userUnivName; //로그인한 유저의 대학교 이름
    private Long userDeptId; //로그인 유저의 학과 ID

    // offset 자동 계산 메서드
    public void calcOffset() {
        this.offset = (this.page - 1) * this.amount;
    }
    
    public String getUserUnivName() {
        return userUnivName;
    }

    public void setUserUnivName(String userUnivName) {
        this.userUnivName = userUnivName;
    }

    public Long getUserDeptId() {
        return userDeptId;
    }

    public void setUserDeptId(Long userDeptId) {
        this.userDeptId = userDeptId;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }

    public List<String> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<String> gradeList) {
        this.gradeList = gradeList;
    }

    public int getView_count() {
        return viewCount;
    }
    public void setView_count(int view_count) {
        this.viewCount = view_count;
    }
    public int getFavorite_count() {
        return favoriteCount;
    }
    public void setFavorite_count(int favorite_count) {
        this.favoriteCount = favorite_count;
    }
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

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }
}
