package com.soldesk.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk.service.CertVerifyService;
import com.soldesk.service.MemberService;
import com.soldesk.service.UnivService;
import com.soldesk.validation.MemberValidation;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorSignupVO;


@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private MemberValidation memberValidation; // 회원가입 유효성 검증을 위한 MemberValidation 주입

    @Autowired
    private MemberService memberService; // 회원가입 처리를 위한 MemberService 주입

    @Autowired
    private UnivService univService;

    @Autowired
    private CertVerifyService certVerifyService; // 자격증 이름 인증(FastAPI) 서비스

    // 자격증 인증 성공 시, 인증된 이름을 세션에 저장하는 키
    private static final String MENTOR_CERT_VERIFIED = "mentorCertVerified";

    @InitBinder({"signupMember"})
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(memberValidation);
    } // MemberValidation을 WebDataBinder에 등록하여 유효성 검증 수행/컨트롤러 실행 전 검증 수행
    
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupMember", new MemberVO());
        model.addAttribute("univList", univService.getAllUniv());
        return "auth/signup"; 
    }
    @PostMapping("/signup")
    public String signupPost(@Valid @ModelAttribute("signupMember") MemberVO member, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println("❌ 진짜 에러 원인: " + error.toString()));
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

    // 멘토 회원가입 폼
    @GetMapping("/signup/mentor")
    public String mentorForm(Model model) {
        if (!model.containsAttribute("signupMentor")) {
            model.addAttribute("signupMentor", new MentorSignupVO());
        }
        model.addAttribute("certVerified", false);
        return "auth/signup-mentor";
    }

    /**
     * 자격증 이미지 + 이름을 받아 FastAPI 로 이름 대조.
     * 성공 시 인증된 이름을 세션에 저장해두고, 실제 가입 시 이 값을 다시 확인한다(우회 방지).
     * AJAX(JSON) 응답: { "matched": true/false, "detail": "..." }
     */
    @PostMapping("/signup/mentor/verify-cert")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyMentorCert(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("matched", false, "detail", "이름을 입력해주세요."));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("matched", false, "detail", "자격증 이미지를 첨부해주세요."));
        }

        try {
            boolean matched = certVerifyService.isMatched(name, file);
            if (matched) {
                session.setAttribute(MENTOR_CERT_VERIFIED, name.trim());
                return ResponseEntity.ok(Map.of("matched", true));
            }
            session.removeAttribute(MENTOR_CERT_VERIFIED);
            return ResponseEntity.ok(Map.of("matched", false, "detail", "이름과 자격증이 일치하지 않아요."));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("matched", false, "detail", "파일 처리 중 오류가 발생했어요."));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(
                Map.of("matched", false, "detail", "인증 서버에 연결할 수 없어요. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/signup/mentor")
    public String mentorSignup(@Valid @ModelAttribute("signupMentor") MentorSignupVO form,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model) {

        // 서버 측 자격증 인증 확인 (클라이언트 우회 방지)
        boolean certVerified = isCertVerified(session, form.getName());
        model.addAttribute("certVerified", certVerified);

        // 1) 형식 검증(@Valid)
        if (bindingResult.hasErrors()) {
             bindingResult.getAllErrors().forEach(error -> System.out.println("에러 원인: " + error.toString()));
            return "auth/signup-mentor";
        }
        // 2) 비밀번호 일치
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "mismatch", "비밀번호가 일치하지 않아요.");
        }
        // 3) 중복 검사 — 일반 회원과 같은 member 테이블을 쓰므로 동일하게 검사
        if (memberService.countByLoginId(form.getLogin_id()) > 0) {
            bindingResult.rejectValue("login_id", "duplicate", "이미 사용 중인 아이디예요.");
        }
        if (memberService.countByEmail(form.getEmail()) > 0) {
            bindingResult.rejectValue("email", "duplicate", "이미 가입된 이메일이에요.");
        }
        // 4) 자격증 인증 필수
        if (!certVerified) {
            model.addAttribute("certError", "자격증 인증을 먼저 완료해주세요.");
        }
        if (bindingResult.hasErrors() || !certVerified) {
             bindingResult.getAllErrors().forEach(error -> System.out.println("에러 원인: " + error.toString()));
            return "auth/signup-mentor";
        }
        // 5) 저장
        try {
            memberService.registerMentor(form);
            session.removeAttribute(MENTOR_CERT_VERIFIED); // 사용 후 정리
        } catch (DuplicateKeyException e) {
            // 동시 요청 안전망
            bindingResult.rejectValue("login_id", "duplicate", "이미 사용 중인 아이디 또는 이메일이에요.");
            return "auth/signup-mentor";
        }
 
        return "redirect:/auth/login?joined=mentor";
    }

    /** 세션에 저장된 인증 이름이 현재 입력한 이름과 같은지 확인 */
    private boolean isCertVerified(HttpSession session, String name) {
        Object verifiedName = session.getAttribute(MENTOR_CERT_VERIFIED);
        return verifiedName != null && name != null && verifiedName.equals(name.trim());
    }
}
