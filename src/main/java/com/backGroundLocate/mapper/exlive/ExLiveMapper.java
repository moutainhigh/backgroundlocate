package com.backGroundLocate.mapper.exlive;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ExLiveMapper {
    Integer selectVehicleIdBySimNumber(@Param("simNumber") String simNumber);
}
