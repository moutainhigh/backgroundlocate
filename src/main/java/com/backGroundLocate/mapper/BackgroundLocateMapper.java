package com.backGroundLocate.mapper;

import com.backGroundLocate.entity.BackgroundLocate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface BackgroundLocateMapper {

    void saveLocation(BackgroundLocate backgroundLocate);

    void deleteLocation(@Param("id") int id);
}
