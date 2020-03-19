package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.CarInfo;
import com.backGroundLocate.mapper.CarInfoMapper;
import com.backGroundLocate.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarInfoServiceImpl implements CarInfoService {

    @Autowired
    CarInfoMapper carInfoMapper;


    @Override
    public List<CarInfo> selectCarListByInternalId(int internalId) {
        return carInfoMapper.selectCarListByInternalId(internalId);
    }

    @Override
    public List<CarInfo> selectCarListByDept(int dept) {
        return carInfoMapper.selectCarListByDept(dept);
    }
}
