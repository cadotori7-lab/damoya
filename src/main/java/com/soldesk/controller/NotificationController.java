package com.soldesk.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soldesk.service.MemberService;
import com.soldesk.service.NotificationService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.NotificationVO;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    @ResponseBody
    public List<NotificationVO> list(Principal principal) {
        if (principal == null) {
            return Collections.emptyList();
        }
        MemberVO member = memberService.findByLoginId(principal.getName());
        return notificationService.getNotifications(member.getMember_id());
    }

    @PostMapping("/read/{notiId}")
    @ResponseBody
    public void read(@PathVariable("notiId") Long notiId, Principal principal) {
        if (principal == null) {
            return;
        }
        notificationService.markAsRead(notiId);
    }
    @PostMapping("/delete/{notiId}")
    @ResponseBody
    public void delete(@PathVariable("notiId") Long notiId, Principal principal) {
        if (principal == null) {
            return;
        }
        notificationService.deleteNotificationById(notiId);
    }
}
