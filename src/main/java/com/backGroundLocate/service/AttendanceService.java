package com.backGroundLocate.service;

import com.backGroundLocate.entity.AttLeave;
import com.backGroundLocate.entity.Attendance;

import java.util.List;
import java.util.Map;

public interface AttendanceService {

    void saveAttendance(Attendance attendance);

    Attendance selectAttendanceForToday(Map paramMap);

    void updateAttendance(Attendance attendance);

    int createLeave(AttLeave attLeave);

    List<AttLeave> selectLeave(Map paramMap);

    AttLeave selectLeaveById(int id);

    void updateLeave(AttLeave attLeave);

    Attendance selectAttendanceForDay(Map paramMap);

    List<Attendance> selectAttendance(Map paramMap);
}
