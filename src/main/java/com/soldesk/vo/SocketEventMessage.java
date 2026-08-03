package com.soldesk.vo;

public class SocketEventMessage {
    private String event;
    private NotificationVO message;

    public SocketEventMessage() {
    }

    public SocketEventMessage(String event, NotificationVO message) {
        this.event = event;
        this.message = message;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public NotificationVO getMessage() {
        return message;
    }

    public void setMessage(NotificationVO message) {
        this.message = message;
    }    
}
