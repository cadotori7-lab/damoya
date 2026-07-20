package com.soldesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.MemberService;
import com.soldesk.service.UnivService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.UnivVO;


@Controller
@RequestMapping("/mypage")
public class MypageController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UnivService univService;
    
    @GetMapping("/index")
    public String index(Model model) {
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberVO member = memberService.findByLoginId(member_id);
        UnivVO univ = univService.getUnivByDeptId(member.getDept_id()); // 학생의 대학 이름과 학과 이름 조회
        model.addAttribute("member", member);
        model.addAttribute("univList", univService.getAllUniv());
        model.addAttribute("univ", univ);
        return "mypage/index";
    }

    @PostMapping("/update")
    public String update(MemberVO member) {
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();
        member.setLogin_id(member_id); // 로그인한 사용자의 ID를 설정
        memberService.updateMember(member); // 회원 정보 업데이트
        return "redirect:/mypage/index"; // 업데이트 후 마이페이지로 리다이렉트
    }
}
