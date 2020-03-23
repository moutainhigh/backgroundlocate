package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.BackgroundLocateCar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface BackgroundLocateCarNewestMapper {

    void saveLocationOfNewest(BackgroundLocateCar backgroundLocateCar);

    void deleteLocationOfNewest(BackgroundLocateCar backgroundLocateCar);

    Map selectCarLocationForNewest(@Param("id") int id);
}
