package com.soldesk.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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

    @Autowired
    private CertVerifyService certVerifyService; // 자격증 이름 인증(FastAPI) 서비스

    // 자격증 이미지 인증 성공 시: 회원 이름 + 인증된 자격증명 목록을 세션에 저장
    private static final String MENTOR_CERT_VERIFIED_NAME = "mentorCertVerifiedName";
    private static final String MENTOR_CERT_VERIFIED_LABELS = "mentorCertVerifiedLabels";

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

    // 멘토 회원가입 폼
    @GetMapping("/signup/mentor")
    public String mentorForm(Model model) {
        if (!model.containsAttribute("signupMentor")) {
            model.addAttribute("signupMentor", new MentorSignupVO());
        }
        model.addAttribute("certVerified", false);
        model.addAttribute("univList", univService.getAllUniv());
        return "auth/signup-mentor";
    }

    /**
     * 자격증 이미지 + 회원 이름을 받아 FastAPI 로 이름 대조.
     * 성공 시 해당 자격증명(certLabel)을 세션에 저장해, 가입 시 입력한 자격증마다 인증 여부를 확인한다.
     */
    @PostMapping("/signup/mentor/verify-cert")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyMentorCert(
            @RequestParam("name") String name,
            @RequestParam("certLabel") String certLabel,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("matched", false, "detail", "이름을 입력해주세요."));
        }
        if (certLabel == null || certLabel.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("matched", false, "detail", "자격증명을 입력해주세요."));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("matched", false, "detail", "자격증 이미지를 첨부해주세요."));
        }

        try {
            boolean matched = certVerifyService.isMatched(name, file);
            if (matched) {
                rememberVerifiedCert(session, name.trim(), certLabel.trim());
                return ResponseEntity.ok(Map.of("matched", true));
            }
            forgetVerifiedCert(session, certLabel.trim());
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

        model.addAttribute("univList", univService.getAllUniv());

        // 1) 형식 검증(@Valid)
        if (bindingResult.hasErrors()) {
             bindingResult.getAllErrors().forEach(error -> System.out.println("에러 원인: " + error.toString()));
            return "auth/signup-mentor";
        }
        // 2) 비밀번호 일치
        if (!form.getPassword().equals(form.getPassword_confirm())) {
            bindingResult.rejectValue("password_confirm", "mismatch", "비밀번호가 일치하지 않아요.");
        }
        // 3) 중복 검사 — 일반 회원과 같은 member 테이블을 쓰므로 동일하게 검사
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
        // 4) 자격증을 입력했다면 각 자격증 이미지 인증이 필수
        String normalizedCert = normalizeCert(form.getCert());
        form.setCert(normalizedCert);
        if (normalizedCert != null && !areAllCertsVerified(session, form.getName(), normalizedCert)) {
            model.addAttribute("certError", "입력한 자격증은 모두 이미지 인증을 완료해주세요.");
            model.addAttribute("certVerified", false);
            return "auth/signup-mentor";
        }
        // 5) 저장
        try {
            memberService.registerMentor(form);
            clearCertVerification(session);
        } catch (DuplicateKeyException e) {
            // 동시 요청 안전망
            bindingResult.rejectValue("login_id", "duplicate", "이미 사용 중인 아이디 또는 이메일이에요.");
            return "auth/signup-mentor";
        }

        return "redirect:/auth/login?joined=mentor";
    }

    private String normalizeCert(String cert) {
        if (cert == null) {
            return null;
        }
        String joined = java.util.Arrays.stream(cert.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        return joined.isEmpty() ? null : joined;
    }

    @SuppressWarnings("unchecked")
    private void rememberVerifiedCert(HttpSession session, String memberName, String certLabel) {
        Object savedName = session.getAttribute(MENTOR_CERT_VERIFIED_NAME);
        if (savedName == null || !memberName.equals(String.valueOf(savedName))) {
            session.setAttribute(MENTOR_CERT_VERIFIED_NAME, memberName);
            session.setAttribute(MENTOR_CERT_VERIFIED_LABELS, new java.util.HashSet<String>());
        }
        java.util.Set<String> labels = (java.util.Set<String>) session.getAttribute(MENTOR_CERT_VERIFIED_LABELS);
        if (labels == null) {
            labels = new java.util.HashSet<>();
            session.setAttribute(MENTOR_CERT_VERIFIED_LABELS, labels);
        }
        labels.add(certLabel);
    }

    @SuppressWarnings("unchecked")
    private void forgetVerifiedCert(HttpSession session, String certLabel) {
        java.util.Set<String> labels = (java.util.Set<String>) session.getAttribute(MENTOR_CERT_VERIFIED_LABELS);
        if (labels != null) {
            labels.remove(certLabel);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean areAllCertsVerified(HttpSession session, String memberName, String cert) {
        if (memberName == null || memberName.trim().isEmpty()) {
            return false;
        }
        Object savedName = session.getAttribute(MENTOR_CERT_VERIFIED_NAME);
        if (savedName == null || !memberName.trim().equals(String.valueOf(savedName))) {
            return false;
        }
        java.util.Set<String> labels = (java.util.Set<String>) session.getAttribute(MENTOR_CERT_VERIFIED_LABELS);
        if (labels == null || labels.isEmpty()) {
            return false;
        }
        for (String part : cert.split(",")) {
            String label = part.trim();
            if (!label.isEmpty() && !labels.contains(label)) {
                return false;
            }
        }
        return true;
    }

    private void clearCertVerification(HttpSession session) {
        session.removeAttribute(MENTOR_CERT_VERIFIED_NAME);
        session.removeAttribute(MENTOR_CERT_VERIFIED_LABELS);
    }
}
