package com.backGroundLocate.mapper;

import com.backGroundLocate.entity.BackgroundLocateCar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface BackgroundLocateCarMapper {

    void saveLocation(BackgroundLocateCar backgroundLocateCar);

    void deleteLocation(@Param("id") int id);
}
