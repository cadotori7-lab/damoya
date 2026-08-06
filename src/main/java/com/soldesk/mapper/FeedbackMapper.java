package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.FeedbackVO;

public interface FeedbackMapper {

    void insertFeedback(FeedbackVO feedback); // 피드백 등록

    List<FeedbackVO> selectFeedbacksByProjectId(long projectId); // 프로젝트별 피드백 목록 (최신순)

    List<FeedbackVO> selectFeedbacksByMentorId(long mentorId); // 멘토가 남긴 피드백 목록 (최신순, 프로젝트 제목 포함)
}
