package com.soldesk.vo;

import java.time.LocalDateTime;
import java.util.List;

public class TalentVO {
    
    private long postId; //게시글 아이디
    private long memberId; // 회원 아이디
    private String kind; // Member/Mentor
    private String title; //게시글 제목
    private String content; //게시글 내용
    private String category; // 카테고리(공모전/학과/교양/교내)
    private String availableTime; // 가능 시간
    private String targetGrade;  // 대상 학년 
    private LocalDateTime createdAt; // 생성일시
    private String matchScope; //매칭범위
    private String field; // 희망 분야
    private String tags; //태그
    private int viewCount; // 게시물 조회 수
    private int favoriteCount; // 관심 등록 수 
    private int page = 1;            // 현재 페이지 (기본값 1)
    private int amount = 6;          // 페이지당 보여줄 개수 (기본값 6)
    private int offset = 0;              // DB 쿼리용 시작 위치
    private String keyword;          // 검색어
    private String tab;              // 상단 탭 상태 (all, RECRUITING, CLOSED)
    private String sort;             // 정렬 기준
    private List<String> categoryList; // 다중 카테고리 체크박스 값
    private List<String> gradeList;    // 다중 학년 체크박스 값
    private boolean liked; // 현재 로그인한 유저가 좋아요를 눌렀는지 여부
    private int commentCount; //댓글 수 
    private String memberName;  // 회원이름
    private String memberMajor; // 소속 학과
    private String memberGrade; // 학년

    
    
    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }
    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    public String getMemberMajor() {
        return memberMajor;
    }
    public void setMemberMajor(String memberMajor) {
        this.memberMajor = memberMajor;
    }
    public String getMemberGrade() {
        return memberGrade;
    }
    public void setMemberGrade(String memberGrade) {
        this.memberGrade = memberGrade;
    }
    public long getPostId() {
        return postId;
    }
    public void setPostId(long postId) {
        this.postId = postId;
    }
    public long getMemberId() {
        return memberId;
    }
    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getTargetGrade() {
        return targetGrade;
    }
    public void setTargetGrade(String targetGrade) {
        this.targetGrade = targetGrade;
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
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
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
    public List<String> getGradeList() {
        return gradeList;
    }
    public void setGradeList(List<String> gradeList) {
        this.gradeList = gradeList;
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
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getAvailableTime() {
        return availableTime;
    }
    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    


}
