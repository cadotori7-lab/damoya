package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.MentorDocument;
import com.soldesk.vo.MentorSignupVO;
import com.soldesk.vo.MentorVO;

public interface MentorMapper {

    void insertMemberForMentor(MentorSignupVO vo); // 멘토 가입 - member 테이블 INSERT

    void insertMentor(MentorSignupVO vo); // 멘토 가입 - mentor 테이블 INSERT

    int selectMentorById(long member_id); // 멘토인지 확인

    MentorVO findByMemberId(long member_id); // 멘토 상세

    List<MentorDocument> findAllMentors(); // 멘토 인덱싱용 회원 + 멘토 정보 조회

    int countMentoringTeams(long memberId); // 멘토링 중인 팀 수

    int countActiveProjects(long memberId); // 진행 중 프로젝트 수

    int countFeedbacks(long memberId); // 내가 남긴 피드백 수
}
