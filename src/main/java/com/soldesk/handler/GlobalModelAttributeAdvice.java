package com.soldesk.handler;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.soldesk.service.MemberService;
import com.soldesk.service.MentorService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorVO;

@ControllerAdvice // 모든 컨트롤러의 모델에 공통 데이터를 채워주는 클래스
public class GlobalModelAttributeAdvice {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MentorService mentorService;

    // header.jsp가 ${member...}를 참조하므로, 로그인한 경우 모든 컨트롤러에 자동으로 채워준다.
    // 각 컨트롤러가 필요에 따라 model.addAttribute("member", ...)로 덮어써도 무방하다.
    @ModelAttribute("member")
    public MemberVO member(Principal principal) {
        if (principal == null) {
            return null;
        }
        return memberService.findByLoginId(principal.getName());
    }

    @ModelAttribute("isMentor")
    public boolean isMentor(Principal principal) {
        if (principal == null) {
            return false;
        }
        MemberVO member = memberService.findByLoginId(principal.getName());
        return mentorService.isMentor(member.getMember_id());
    }

    // header.jsp가 멘토일 때 career/cert/field를 보여줄 수 있도록 전역으로 채워준다.
    @ModelAttribute("mentor")
    public MentorVO mentor(Principal principal) {
        if (principal == null) {
            return null;
        }
        MemberVO member = memberService.findByLoginId(principal.getName());
        if (!mentorService.isMentor(member.getMember_id())) {
            return null;
        }
        return mentorService.getMentorInfo(member.getMember_id());
    }
}
