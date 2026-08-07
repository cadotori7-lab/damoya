package com.soldesk.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.FeedbackMapper;
import com.soldesk.vo.FeedbackVO;
import com.soldesk.vo.ProjectVO;

@Service
public class FeedbackService {

    private static final List<String> STAGES = Arrays.asList("MIDTERM", "FINAL", "REVIEW");

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private NotificationService notificationService;

    /** 해당 프로젝트의 합류(JOINED)한 멘토인지 확인 */
    @Transactional(readOnly = true)
    public boolean isProjectMentor(long projectId, long memberId) {
        return "MENTOR".equals(participationService.getProjectRole(projectId, memberId));
    }

    @Transactional(readOnly = true)
    public List<FeedbackVO> getFeedbacksByProject(long projectId) {
        return feedbackMapper.selectFeedbacksByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<FeedbackVO> getFeedbacksByMentor(long mentorId) {
        return feedbackMapper.selectFeedbacksByMentorId(mentorId);
    }

    /** 피드백 등록 (담당 멘토만 가능) + 팀장에게 알림 */
    @Transactional
    public void writeFeedback(long projectId, long mentorId, String stage, String content) {
        if (!isProjectMentor(projectId, mentorId)) {
            throw new AccessDeniedException("해당 프로젝트의 멘토만 피드백을 남길 수 있습니다.");
        }
        if (!STAGES.contains(stage)) {
            throw new IllegalArgumentException("평가 단계를 선택해주세요.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("피드백 내용을 입력해주세요.");
        }

        FeedbackVO feedback = new FeedbackVO();
        feedback.setProjectId(projectId);
        feedback.setMentorId(mentorId);
        feedback.setStage(stage);
        feedback.setContent(content.trim());
        feedbackMapper.insertFeedback(feedback);

        ProjectVO project = projectService.getProjectById(projectId);
        if (project != null && project.getOwnerId() != null) {
            notificationService.toMessage(projectId, project.getOwnerId().intValue(),
                    "FEEDBACK", "멘토 피드백 등록- " + project.getTitle());
        }
    }
}
