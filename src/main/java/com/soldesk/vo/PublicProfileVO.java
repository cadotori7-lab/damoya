package com.soldesk.vo;

/**
 * 공개 프로필 응답 VO.
 *
 * 남에게 보여줘도 되는 필드만 담는다.
 * 이메일·로그인아이디·계정상태 등 민감정보는 절대 넣지 않는다
 * (프론트에서 거르는 게 아니라 서버에서 아예 안 내려주는 것이 핵심).
 *
 * profile_public = false 인 회원은 서비스에서 최소 정보만 채워 반환한다.
 */
public class PublicProfileVO {

    private int    memberId;
    private String  name;
    private String  deptName;     // 학과 (조인)
    private Integer grade;        // 학년
    private String  major;        // 주전공
    private String  intro;        // 소개글
    private boolean approved;     // 학교 인증 배지 표시용
    private boolean isMentor;     // 멘토 배지 표시용
    private boolean isPublic;     // 공개 여부 (false면 상세 숨김)

    /* 멘토일 때만 채워지는 필드 */
    private String  field;        // 전문분야
    private String  career;       // 경력

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public boolean isMentor() { return isMentor; }
    public void setMentor(boolean isMentor) { this.isMentor = isMentor; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public String getCareer() { return career; }
    public void setCareer(String career) { this.career = career; }
}
