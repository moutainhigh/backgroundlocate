package com.backGroundLocate.mapper;

import com.backGroundLocate.entity.BackgroundLocateUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface BackgroundLocateUserNewestMapper {

    void saveLocationOfNewest(BackgroundLocateUser backgroundLocateUser);

    void deleteLocationOfNewest(BackgroundLocateUser backgroundLocateUser);

    Map selectUserLocationForNewest(@Param("id") int id);
}
