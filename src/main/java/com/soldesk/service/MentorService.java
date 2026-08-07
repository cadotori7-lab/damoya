package com.soldesk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.soldesk.mapper.MentorMapper;
import com.soldesk.vo.MentorDashboardStatsVO;
import com.soldesk.vo.MentorTeamVO;
import com.soldesk.vo.MentorVO;

@Service
public class MentorService {

    @Autowired
    private MentorMapper mentorMapper;

    @Transactional(readOnly = true)
    public MentorDashboardStatsVO getDashboardStats(int memberId) {
        MentorDashboardStatsVO stats = new MentorDashboardStatsVO();
        stats.setMentoringTeamCount(mentorMapper.countMentoringTeams(memberId));
        stats.setActiveProjectCount(mentorMapper.countActiveProjects(memberId));
        stats.setFeedbackCount(mentorMapper.countFeedbacks(memberId));
        stats.setPendingOfferCount(mentorMapper.countPendingOffers(memberId));
        return stats;
    }

    @Transactional(readOnly = true)
    public boolean isMentor(long memberId) {
        return mentorMapper.selectMentorById(memberId) > 0;
    }

    @Transactional(readOnly = true)
    public MentorVO getMentorInfo(int memberId) {
        return mentorMapper.findByMemberId(memberId);
    }

    /** 멘토링 중인 팀 카드 목록 (완료되지 않은 프로젝트만) */
    @Transactional(readOnly = true)
    public List<MentorTeamVO> getMentoringTeams(int memberId) {
        return mentorMapper.findMentoringTeams(memberId);
    }
}
