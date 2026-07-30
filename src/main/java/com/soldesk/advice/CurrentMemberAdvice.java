package com.soldesk.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.vo.MemberVO;

/** 모든 뷰에 로그인 회원(member)을 넣어 헤더 알림/아바타가 동작하게 한다. */
@ControllerAdvice
public class CurrentMemberAdvice {

    @Autowired
    private MemberMapper memberMapper;

    @ModelAttribute("member")
    public MemberVO currentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        String name = auth.getName();
        MemberVO member = memberMapper.findByLoginId(name);
        if (member == null) {
            member = memberMapper.findByEmail(name);
        }
        return member;
    }
}
