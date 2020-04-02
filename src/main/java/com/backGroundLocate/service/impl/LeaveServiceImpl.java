package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.Leave;
import com.backGroundLocate.mapper.main.LeaveMapper;
import com.backGroundLocate.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveMapper leaveMapper;

    @Override
    public void saveLeave(Leave leave) {
        leaveMapper.saveLeave(leave);
    }

    @Override
    public List<Leave> selectLeaveList(Map paramMap) {
        return leaveMapper.selectLeaveList(paramMap);
    }

    @Override
    public Leave selectLeave(Map paramMap) {
        return leaveMapper.selectLeave(paramMap);
    }
}
