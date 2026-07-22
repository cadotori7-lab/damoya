package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soldesk.mapper.UnivMapper;
import com.soldesk.vo.UnivVO;

@Service
public class UnivService {

    @Autowired
    private UnivMapper univMapper;

    public List<UnivVO> getAllUniv() {
        return univMapper.getAllUniv();
    }

    public UnivVO getUnivByDeptId(int dept_id) {
        return univMapper.getUnivByDeptId(dept_id);
    }
}