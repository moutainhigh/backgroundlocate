package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.CarInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface CarInfoMapper {

    List<CarInfo> selectCarListByDept(@Param("deptId") int deptId);

    List<CarInfo> selectCarList(CarInfo carInfo);

    List<CarInfo> selectCarForNewest(Map<String,Object> paramMap);

    CarInfo selectCar(CarInfo carInfo);
}
