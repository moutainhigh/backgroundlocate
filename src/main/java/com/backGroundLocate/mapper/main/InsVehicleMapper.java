package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.InsVehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface InsVehicleMapper {
    int createVehicle(InsVehicle insVehicle);

    List<InsVehicle> selectVehicle(Map paramMap);

    InsVehicle selectVehicleById(@Param("id") int id);

    void updateVehicle(InsVehicle insVehicle);

    void deleteVehicle(@Param("id") int id);

    List<InsVehicle> selectDirectlyVehicle(@Param("deptId") int deptId);

}
