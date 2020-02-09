package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.InternalInfo;
import com.backGroundLocate.mapper.InternalInfoMapper;
import com.backGroundLocate.service.InternalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternalServiceImpl implements InternalInfoService {

    @Autowired
    private InternalInfoMapper internalInfoMapper;

    @Override
    public List<InternalInfo> selectInternalListByDeptId(int deptId) {
        return internalInfoMapper.selectInternalListByDeptId(deptId);
    }
}
