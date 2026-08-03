package com.soldesk.vo;

public class NotificationVO {
    private Long noti_id;
    private Long member_id;
    private String kind;
    private String content;
    private Boolean is_read;
    private Long target_id;

    public NotificationVO() {
    }
    public NotificationVO(Long noti_id, Long member_id, String kind, String content, Boolean is_read) {
        this.noti_id = noti_id;
        this.member_id = member_id;
        this.kind = kind;
        this.content = content;
        this.is_read = is_read;
    }

    public Long getNoti_id() {
        return noti_id;
    }

    public void setNoti_id(Long noti_id) {
        this.noti_id = noti_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIs_read() {
        return is_read;
    }

    public void setIs_read(Boolean is_read) {
        this.is_read = is_read;
    }

    public Long getTarget_id() {
        return target_id;
    }

    public void setTarget_id(Long target_id) {
        this.target_id = target_id;
    }

    

    
}
