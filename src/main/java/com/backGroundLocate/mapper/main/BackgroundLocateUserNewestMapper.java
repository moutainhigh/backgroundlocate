package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.BackgroundLocateUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface BackgroundLocateUserNewestMapper {

    void saveLocationOfNewest(BackgroundLocateUser backgroundLocateUser);

    void deleteLocationOfNewest(BackgroundLocateUser backgroundLocateUser);

    List<Map> selectUserLocationForNewest(Map<String,String> paramMap);
}
