package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface AttendanceMapper {

    void saveAttendance(Attendance attendance);

    Attendance selectAttendanceForToday(Map paramMap);

}
