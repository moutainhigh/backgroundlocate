package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.BnsAreaPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface BnsAreaPointMapper {

    int createAreaPoint(BnsAreaPoint bnsAreaPoint);

    List<BnsAreaPoint> selectAreaPoint(Map paramMap);

    void deleteAreaPoint(@Param(value = "areaId") int areaId);
}
