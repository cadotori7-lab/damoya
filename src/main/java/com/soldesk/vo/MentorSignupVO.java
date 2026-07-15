package com.soldesk.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 멘토 회원가입 폼 VO.
 *
 * 멘토 가입은 테이블 2개에 나눠 저장된다(ERD의 슈퍼/서브타입 구조).
 *   - member : login_id, email, password, name, dept_id, intro
 *   - mentor : career, cert, field   (member_id 를 공유 PK 로 사용)
 * 화면에서는 한 폼이므로 이 VO 하나로 받고, 서비스에서 두 개로 나눠 INSERT 한다.
 */
public class MentorSignupVO {

    /** member INSERT 후 생성된 PK. mentor INSERT 시 재사용(useGeneratedKeys) */
    private Long member_id;

    /* ---------- 1. 계정 ---------- */

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, message = "아이디는 4자 이상이어야 해요.")
    private String login_id;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 아니에요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 해요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;   // 검증용 (DB 저장 안 함)

    /* ---------- 2. 멘토 정보 (member 공통) ---------- */

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    /** 소속 학과. 교내 교수면 선택, 외부 전문가면 비워둠(NULL 허용) */
    private Long dept_id;

    /** 소개글 — 프로필에 노출 */
    private String intro;

    /* ---------- 3. 멘토 전용 (mentor 테이블) ---------- */

    @NotBlank(message = "전문분야를 입력해주세요.")
    private String field;    // 전문분야

    private String career;   // 경력
    private String cert;     // 자격증


    public Long getMember_id() { return member_id; }
    public void setMember_id(Long member_id) { this.member_id = member_id; }

    public String getLogin_id() { return login_id; }
    public void setLogin_id(String login_id) { this.login_id = login_id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordConfirm() { return passwordConfirm; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getDept_id() { return dept_id; }
    public void setDept_id(Long dept_id) { this.dept_id = dept_id; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public String getCareer() { return career; }
    public void setCareer(String career) { this.career = career; }

    public String getCert() { return cert; }
    public void setCert(String cert) { this.cert = cert; }
}
