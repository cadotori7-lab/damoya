package com.soldesk.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
import com.soldesk.service.UnivService;
import com.soldesk.validation.MemberValidation;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorSignupVO;


@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private MemberValidation memberValidation;

    @Autowired
    private MemberService memberService; 

    @Autowired
    private UnivService univService;

    @InitBinder({"signupMember"})
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(memberValidation);
    } 
    
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupMember", new MemberVO());
        model.addAttribute("univList", univService.getAllUniv());
        return "auth/signup"; 
    }
    @PostMapping("/signup")
    public String signupPost(@Valid @ModelAttribute("signupMember") MemberVO member, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(" 에러 원인: " + error.toString()));
            model.addAttribute("univList", univService.getAllUniv());
            return "auth/signup";
        }
        if (memberService.countByLoginId(member.getLogin_id()) > 0) {
            bindingResult.rejectValue("login_id", "duplicate", "이미 사용 중인 아이디예요.");
        }
        if (memberService.countByEmail(member.getEmail()) > 0) {
            bindingResult.rejectValue("email", "duplicate", "이미 가입된 이메일이에요.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("univList", univService.getAllUniv());
            return "auth/signup";
        }
        try {
            memberService.registerMember(member);
        } catch (DuplicateKeyException e) {
            bindingResult.rejectValue("login_id", "duplicate", "이미 사용 중인 아이디 또는 이메일이에요.");
            model.addAttribute("univList", univService.getAllUniv());
            return "auth/signup";
        }
        return "redirect:/auth/login";
    }
    @GetMapping("/login")
    public String login() {
        return "auth/login"; 
    }

    @GetMapping("/signup/mentor")
    public String mentorForm(Model model) {
        if (!model.containsAttribute("signupMentor")) {
            model.addAttribute("signupMentor", new MentorSignupVO());
        }
        return "auth/signup-mentor";
    }
 
    @PostMapping("/signup/mentor")
    public String mentorSignup(@Valid @ModelAttribute("signupMentor") MentorSignupVO form,
                               BindingResult bindingResult,
                               Model model) {
 
        // 1) 형식 검증(@Valid)
        if (bindingResult.hasErrors()) {
             bindingResult.getAllErrors().forEach(error -> System.out.println("에러 원인: " + error.toString()));
            return "auth/signup-mentor";
        }
        // 2) 비밀번호 일치
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "mismatch", "비밀번호가 일치하지 않아요.");
        }
        // 3) 중복 검사 
        if (memberService.countByLoginId(form.getLogin_id()) > 0) {
            bindingResult.rejectValue("login_id", "duplicate", "이미 사용 중인 아이디예요.");
        }
        if (memberService.countByEmail(form.getEmail()) > 0) {
            bindingResult.rejectValue("email", "duplicate", "이미 가입된 이메일이에요.");
        }
        if (bindingResult.hasErrors()) {
             bindingResult.getAllErrors().forEach(error -> System.out.println("에러 원인: " + error.toString()));
            return "auth/signup-mentor";
        }
        // 4) 저장
        try {
            memberService.registerMentor(form);
        } catch (DuplicateKeyException e) {
            // 동시 요청 안전망
            bindingResult.rejectValue("login_id", "duplicate", "이미 사용 중인 아이디 또는 이메일이에요.");
            return "auth/signup-mentor";
        }
 
        return "redirect:/auth/login?joined=mentor";
    }
}
