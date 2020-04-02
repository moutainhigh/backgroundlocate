package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.Leave;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface LeaveMapper {

    void saveLeave(Leave leave);

    List<Leave> selectLeaveList(Map paramMap);

    Leave selectLeave (Map paramMap);
}
