package com.soldesk.handler;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.soldesk.service.MemberService;
import com.soldesk.vo.MemberVO;

@ControllerAdvice // 모든 컨트롤러의 모델에 공통 데이터를 채워주는 클래스
public class GlobalModelAttributeAdvice {

    @Autowired
    private MemberService memberService;

    // header.jsp가 ${member...}를 참조하므로, 로그인한 경우 모든 컨트롤러에 자동으로 채워준다.
    // 각 컨트롤러가 필요에 따라 model.addAttribute("member", ...)로 덮어써도 무방하다.
    @ModelAttribute("member")
    public MemberVO member(Principal principal) {
        if (principal == null) {
            return null;
        }
        return memberService.findByLoginId(principal.getName());
    }
}
