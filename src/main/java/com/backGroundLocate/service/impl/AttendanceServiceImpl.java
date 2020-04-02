package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.Attendance;
import com.backGroundLocate.mapper.main.AttendanceMapper;
import com.backGroundLocate.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

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
}
