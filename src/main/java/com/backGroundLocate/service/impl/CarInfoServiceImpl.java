package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.CarInfo;
import com.backGroundLocate.mapper.main.CarInfoMapper;
import com.backGroundLocate.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CarInfoServiceImpl implements CarInfoService {

    @Autowired
    CarInfoMapper carInfoMapper;

    @Override
    public List<CarInfo> selectCarListByDept(int deptId) {
        return carInfoMapper.selectCarListByDept(deptId);
    }

    @Override
    public List<CarInfo> selectCarList(CarInfo carInfo) {
        return carInfoMapper.selectCarList(carInfo);
    }

    @Override
    public List<CarInfo> selectCarForNewest(Map<String, Object> paramMap) {
        return carInfoMapper.selectCarForNewest(paramMap);
    }

    @Override
    public CarInfo selectCar(CarInfo carInfo) {
        return carInfoMapper.selectCar(carInfo);
    }
}
