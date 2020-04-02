package com.backGroundLocate.service;

import com.backGroundLocate.entity.Leave;

import java.util.List;
import java.util.Map;

public interface LeaveService {

    void saveLeave(Leave leave);

    List<Leave> selectLeaveList(Map paramMap);

    Leave selectLeave (Map paramMap);
}
