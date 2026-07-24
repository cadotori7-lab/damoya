package com.soldesk.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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
import com.soldesk.validation.MentorSignupValidation;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorSignupVO;


@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private MemberValidation memberValidation;

    @Autowired
    private MentorSignupValidation mentorSignupValidation;

    @Autowired
    private MemberService memberService;

    @Autowired
    private UnivService univService;
    // 회원가입 폼 검증
    @InitBinder({"signupMember"})
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(memberValidation);
    }
    // 외부 전문가 회원가입 폼 검증
    @InitBinder({"signupMentor"})
    public void initMentorBinder(WebDataBinder binder) {
        // 외부 전문가는 dept_id를 비워둘 수 있어서, 빈 문자열이 전달되면 null로 처리
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
        binder.addValidators(mentorSignupValidation);
    }
    // 회원가입 폼
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupMember", new MemberVO());
        model.addAttribute("univList", univService.getAllUniv());
        return "auth/signup"; 
    }
    // 회원가입 처리
    @PostMapping("/signup")
    public String signupPost(@Valid @ModelAttribute("signupMember") MemberVO member, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(" 에러 원인: " + error.toString()));
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
    // 로그인 페이지
    @GetMapping("/login")
    public String login() {
        return "auth/login"; 
    }
    // 멘토 로그인 페이지
    @GetMapping("/signup/mentor")
    public String mentorForm(Model model) {
        if (!model.containsAttribute("signupMentor")) {
            model.addAttribute("signupMentor", new MentorSignupVO());
        }
        model.addAttribute("univList", univService.getAllUniv());
        return "auth/signup-mentor";
    }
    // 멘토 회원가입 처리
    @PostMapping("/signup/mentor")
    public String mentorSignup(@Valid @ModelAttribute("signupMentor") MentorSignupVO form,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
             bindingResult.getAllErrors().forEach(error -> System.out.println("에러 원인: " + error.toString()));
            model.addAttribute("univList", univService.getAllUniv());
            return "auth/signup-mentor";
        }
        try {
            memberService.registerMentor(form);
        } catch (DuplicateKeyException e) {
            // 동시 요청 안전망
            bindingResult.rejectValue("login_id", "duplicate", "이미 사용 중인 아이디 또는 이메일이에요.");
            model.addAttribute("univList", univService.getAllUniv());
            return "auth/signup-mentor";
        }

        return "redirect:/auth/login?joined=mentor";
    }
}
