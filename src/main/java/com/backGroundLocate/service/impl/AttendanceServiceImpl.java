package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.AttLeave;
import com.backGroundLocate.entity.Attendance;
import com.backGroundLocate.mapper.main.AttLeaveMapper;
import com.backGroundLocate.mapper.main.AttendanceMapper;
import com.backGroundLocate.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private AttLeaveMapper attLeaveMapper;

    @Override
    public void saveAttendance(Attendance attendance) {
        attendanceMapper.saveAttendance(attendance);
    }

    @Override
    public Attendance selectAttendanceForToday(Map paramMap) {
        return attendanceMapper.selectAttendanceForToday(paramMap);
    }

    @Override
    public void updateAttendance(Attendance attendance) {
        attendanceMapper.updateAttendance(attendance);
    }

    @Override
    public int createLeave(AttLeave attLeave) {
        return attLeaveMapper.createLeave(attLeave);
    }

    @Override
    public List<AttLeave> selectLeave(Map paramMap) {
        return attLeaveMapper.selectLeave(paramMap);
    }

    @Override
    public AttLeave selectLeaveById(int id) {
        return attLeaveMapper.selectLeaveById(id);
    }

    @Override
    public void updateLeave(AttLeave attLeave) {
        attLeaveMapper.updateLeave(attLeave);
    }
}
