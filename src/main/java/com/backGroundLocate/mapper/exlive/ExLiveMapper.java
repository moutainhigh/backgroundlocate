package com.backGroundLocate.mapper.exlive;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Mapper
@Component
public interface ExLiveMapper {
    Integer selectVehicleIdBySimNumber(@Param("simNumber") String simNumber);

    String selectVehicleLastRunDate(@Param("simNumber") String simNumber);
}
