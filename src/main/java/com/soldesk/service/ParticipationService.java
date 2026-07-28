package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.vo.ParticipationVO;

@Service
public class ParticipationService {

    @Autowired
    private ParticipationMapper participationMapper;

    // 프로젝트 지원하기
    @Transactional
    public void applyForProject(ParticipationVO vo){
        
        // 디버깅용: 콘솔에 찍히는지 확인해보세요!
        System.out.println(">>> 프로젝트 ID: " + vo.getProjectId() + ", 회원 ID: " + vo.getMemberId());
        
        int existingCount = participationMapper.countByProjectAndMember(vo.getProjectId(), vo.getMemberId());
        System.out.println(">>> 이미 지원한 내역 개수: " + existingCount);
        
        if(existingCount > 0){
            throw new IllegalStateException("이미 지원했거나 참여 중인 프로젝트입니다.");
        }
        
        participationMapper.insertApplication(vo);
    }

    @Transactional
    public List<ParticipationVO> getApplicants(Long projectId){
        return participationMapper.selectApplicationList(projectId);
    }
}
    
    
    
