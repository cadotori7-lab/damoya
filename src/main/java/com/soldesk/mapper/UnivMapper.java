package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.UnivVO;

public interface UnivMapper {
    List<UnivVO> getAllUniv();
    UnivVO getUnivByDeptId(Integer dept_id);
}
