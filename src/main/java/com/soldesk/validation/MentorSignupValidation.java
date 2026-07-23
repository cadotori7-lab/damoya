package com.soldesk.validation;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.soldesk.service.MemberService;
import com.soldesk.vo.MentorSignupVO;

@Component
public class MentorSignupValidation implements Validator {

    @Autowired
    private MemberService memberService;

    @Override
    public boolean supports(Class<?> clazz) {
        return MentorSignupVO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MentorSignupVO form = (MentorSignupVO) target;

        if (!errors.hasFieldErrors("login_id")
            && memberService.countByLoginId(form.getLogin_id()) > 0) {
            errors.rejectValue("login_id", "login_id.duplicate", "이미 사용 중인 아이디예요.");
        }
        if (!errors.hasFieldErrors("email")
            && memberService.countByEmail(form.getEmail()) > 0) {
            errors.rejectValue("email", "email.duplicate", "이미 가입된 이메일이에요.");
        }
        if (!Objects.equals(form.getPassword(), form.getPassword_confirm())) {
            errors.rejectValue("password_confirm", "password.mismatch", "비밀번호가 일치하지 않아요.");
        }
    }
}
