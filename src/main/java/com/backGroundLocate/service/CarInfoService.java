package com.backGroundLocate.service;

import com.backGroundLocate.entity.CarInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CarInfoService {

    List<CarInfo> selectCarListByDept(int deptId);

    List<CarInfo> selectCarList(CarInfo carInfo);

    List<CarInfo> selectCarForNewest(Map<String,Object> paramMap);

    CarInfo selectCar(CarInfo carInfo);

    int selectCarIllegalNum(Map paramMap);
}
