package com.soldesk.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.MemberService;
import com.soldesk.validation.MemberValidation;
import com.soldesk.vo.MemberVO;


@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private MemberValidation memberValidation; // 회원가입 유효성 검증을 위한 MemberValidation 주입

    @Autowired
    private MemberService memberService; // 회원가입 처리를 위한 MemberService 주입

    @InitBinder({"signupMember"})
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(memberValidation);
    } // MemberValidation을 WebDataBinder에 등록하여 유효성 검증 수행/컨트롤러 실행 전 검증 수행
    
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupMember", new MemberVO());
        return "auth/signup"; 
    }
    @PostMapping("/signup")
    public String signupPost(@Valid @ModelAttribute("signupMember") MemberVO member, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println("❌ 진짜 에러 원인: " + error.toString()));
            return "auth/signup";
        }
        memberService.registerMember(member);
        return "redirect:/auth/login";
    }
    @GetMapping("/login")
    public String login() {
        return "auth/login"; 
    }
}
