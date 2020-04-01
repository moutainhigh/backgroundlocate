package com.backGroundLocate.service;

import com.backGroundLocate.entity.Attendance;

import java.util.Map;

public interface AttendanceService {

    void saveAttendance(Attendance attendance);

    Attendance selectAttendanceForToday(Map paramMap);
}
