package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.ResultMapper;
import com.soldesk.vo.ResultVO;

@Service
public class ResultService {

    @Autowired
    private ResultMapper resultMapper;

    @Transactional(readOnly = true)
    public List<ResultVO> getApprovedResults(long projectId) {
        return resultMapper.selectApprovedResults(projectId);
    }
}
