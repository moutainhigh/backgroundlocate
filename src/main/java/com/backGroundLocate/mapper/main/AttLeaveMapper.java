package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.AttLeave;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface AttLeaveMapper {
    int createLeave(AttLeave attLeave);

    List<AttLeave> selectLeave(Map paramMap);

    AttLeave selectLeaveById(@Param("id") int id);

    void updateLeave(AttLeave attLeave);

}
