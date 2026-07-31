package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.NotificationMapper;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.NotificationVO;
import com.soldesk.vo.SocketEventMessage;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private MemberService memberService;

    @Transactional
    public void toMessage(long targetId, int memberId, String kind, String content) {
        NotificationVO notification = new NotificationVO();
        notification.setTarget_id(targetId);
        notification.setMember_id((long) memberId);
        notification.setKind(kind);
        notification.setContent(content);
        notificationMapper.insertNotification((long)memberId, kind, content, targetId);

        // STOMP 세션의 Principal.getName()은 로그인 ID이므로, 회원 고유번호가 아닌 로그인 ID를 대상으로 보내야 한다.
        MemberVO receiver = memberService.getMemberById((long) memberId);
        if (receiver == null) {
            return;
        }
        SocketEventMessage socketEventMessage = new SocketEventMessage("notification", notification);
        messagingTemplate.convertAndSendToUser(receiver.getLogin_id(), "/queue/notifications/" + memberId, socketEventMessage);
    }

    @Transactional
    public List<NotificationVO> getNotifications(int memberId) {
        return notificationMapper.selectNotificationsByMemberId((long) memberId);
    }
    @Transactional
    public void markAsRead(Long notiId) {
        notificationMapper.updateNotificationReadStatus(notiId);
    }
    @Transactional
    public void deleteNotificationById(Long notiId) {
        notificationMapper.deleteNotificationById(notiId);
    }
    @Transactional
    public int countNotificationsByMemberId(int memberId) {
        return notificationMapper.countNotificationsByMemberId((long) memberId);
    }
}
