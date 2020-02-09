package com.backGroundLocate.mapper;

import com.backGroundLocate.entity.BackgroundLocate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface BackgroundLocateNewestMapper {

    void saveLocationOfNewest(BackgroundLocate backgroundLocate);

    void deleteLocationOfNewest(BackgroundLocate backgroundLocate);

    Map selectUserLocationForNewest(@Param("id") int id);
}
