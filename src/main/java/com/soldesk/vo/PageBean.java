package com.soldesk.vo;

public class PageBean {

    private int currentPage; //현재 페이지 번호
    private int contentCnt; //전체 게시글 개수
    private int cotentPageCnt; //페이지당 글 개수
    private int min; //최소페이지 번호
    private int max; //최대페이지 번호
    private int prevPage; //이전 버튼 페이지 번호
    private int nextPage; //다음 버튼 페이지 번호
    private int pageCnt; //전체 페이지 개수

    //현재 페이지 정보, 전체 게시글 개수, 페이지당 글의 개수
    public PageBean(int currentPage, int contentCnt, int contentPageCnt){
        this.contentCnt = contentCnt;
        this.cotentPageCnt = contentPageCnt;
        this.currentPage = currentPage;

        //전체 페이지 개수
        pageCnt = contentCnt / contentPageCnt;
        if(contentCnt % contentPageCnt != 0){
            pageCnt += 1;
        }

        //최소 페이지
        min = ((currentPage-1) / 10) * 10 + 1;

        // 최대페이지
        max = min + 10 - 1;
        if(max > pageCnt){
            max = pageCnt;
        }//최대페이지보다 전체페이지가 크면 최대페이지를 전체페이지 값으로

        prevPage = min - 1; // 이전버튼 클릭 시 최소페이지에서 1페이지 전 페이지값 계산
        nextPage = max + 1; // 다음버튼 클릭 시 최대페이지에서 1페이지 후 페이지값 계산
    }


    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getContentCnt() {
        return contentCnt;
    }
    public void setContentCnt(int contentCnt) {
        this.contentCnt = contentCnt;
    }
    public int getCotentPageCnt() {
        return cotentPageCnt;
    }
    public void setCotentPageCnt(int cotentPageCnt) {
        this.cotentPageCnt = cotentPageCnt;
    }
    public int getMin() {
        return min;
    }
    public void setMin(int min) {
        this.min = min;
    }
    public int getMax() {
        return max;
    }
    public void setMax(int max) {
        this.max = max;
    }
    public int getPrevPage() {
        return prevPage;
    }
    public void setPrevPage(int prevPage) {
        this.prevPage = prevPage;
    }
    public int getNextPage() {
        return nextPage;
    }
    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }
    public int getPageCnt() {
        return pageCnt;
    }
    public void setPageCnt(int pageCnt) {
        this.pageCnt = pageCnt;
    }
    
    
}
