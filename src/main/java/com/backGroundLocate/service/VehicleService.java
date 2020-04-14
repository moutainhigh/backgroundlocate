package com.backGroundLocate.service;

import com.backGroundLocate.entity.InsVehicle;

import java.util.List;
import java.util.Map;

public interface VehicleService {

    int createVehicle(InsVehicle insVehicle);

    List<InsVehicle> selectVehicle(Map paramMap);

    InsVehicle selectVehicleById(int id);

    void updateVehicle(InsVehicle insVehicle);

    void deleteVehicle(int id);

    List<InsVehicle> selectDirectlyVehicle(int deptId);
}
