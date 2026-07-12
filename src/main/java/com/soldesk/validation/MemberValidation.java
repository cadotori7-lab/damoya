package com.soldesk.validation;

import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.soldesk.vo.MemberVO;

@Component
public class MemberValidation implements Validator {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberVO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MemberVO member = (MemberVO) target;

        // 이메일
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors, "email", "email.required", "이메일을 입력하세요.");
        if (member.getEmail() != null 
            && !EMAIL_PATTERN.matcher(member.getEmail()).matches()) {
            errors.rejectValue("email", "email.invalid", 
                "이메일 형식이 올바르지 않습니다.");
        }

        // 비밀번호
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors, "password", "password.required", "비밀번호를 입력하세요.");
        if (member.getPassword() != null 
            && !PASSWORD_PATTERN.matcher(member.getPassword()).matches()) {
            errors.rejectValue("password", "password.invalid",
                "영문, 숫자, 특수문자 조합 8자 이상이어야 합니다.");
        }

        // 비밀번호 확인
        if (!Objects.equals(member.getPassword(), 
                            member.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "password.mismatch", 
                "비밀번호가 일치하지 않습니다.");
        }

    }
}