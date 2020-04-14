package com.backGroundLocate.service.impl;

import com.backGroundLocate.mapper.main.InsVehicleMapper;
import com.backGroundLocate.entity.InsVehicle;
import com.backGroundLocate.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private InsVehicleMapper insVehicleMapper;

    @Override
    public int createVehicle(InsVehicle insVehicle) {
        return insVehicleMapper.createVehicle(insVehicle);
    }

    @Override
    public List<InsVehicle> selectVehicle(Map paramMap) {
        return insVehicleMapper.selectVehicle(paramMap);
    }

    @Override
    public InsVehicle selectVehicleById(int id) {
        return insVehicleMapper.selectVehicleById(id);
    }

    @Override
    public void updateVehicle(InsVehicle insVehicle) {
        insVehicleMapper.updateVehicle(insVehicle);
    }

    @Override
    public void deleteVehicle(int id) {
        insVehicleMapper.deleteVehicle(id);
    }

    @Override
    public List<InsVehicle> selectDirectlyVehicle(int deptId) {
        return insVehicleMapper.selectDirectlyVehicle(deptId);
    }
}
