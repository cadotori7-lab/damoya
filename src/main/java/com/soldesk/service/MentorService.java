package com.soldesk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.MentorMapper;
import com.soldesk.vo.MentorDashboardStatsVO;

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
        return stats;
    }
}
