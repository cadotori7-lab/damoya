package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.NotificationVO;

public interface NotificationMapper {
    void insertNotification(Long member_id, String kind, String content, Long target_id);
    void updateNotificationReadStatus(Long noti_id);
    List<NotificationVO> selectNotificationsByMemberId(Long member_id);
    void deleteNotificationById(Long noti_id);
    int countNotificationsByMemberId(Long member_id);
}
