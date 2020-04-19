package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.*;
import com.backGroundLocate.mapper.main.*;
import com.backGroundLocate.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private BnsIllegalMapper bnsIllegalMapper;

    @Autowired
    private BnsUserLocateMapper bnsUserLocateMapper;

    @Autowired
    private BnsUserNewestLocateMapper bnsUserNewestLocateMapper;

    @Autowired
    private BnsAreaMapper bnsAreaMapper;

    @Autowired
    private BnsAreaPointMapper bnsAreaPointMapper;

    @Override
    public int createIllegal(BnsIllegal bnsIllegal) {
        return bnsIllegalMapper.createIllegal(bnsIllegal);
    }

    @Override
    public List<BnsIllegal> selectIllegal(Map paramMap) {
        return bnsIllegalMapper.selectIllegal(paramMap);
    }

    @Override
    public BnsIllegal selectIllegalById(int id) {
        return bnsIllegalMapper.selectIllegalById(id);
    }

    @Override
    public int selectIllegalNum(Map paramMap) {
        return bnsIllegalMapper.selectIllegalNum(paramMap);
    }

    @Override
    public void createUserLocation(BnsUserLocate bnsUserLocate) {
        bnsUserLocateMapper.createUserLocation(bnsUserLocate);
    }

    @Override
    public void createUserLocationForNewest(BnsUserLocate bnsUserLocate) {
        bnsUserNewestLocateMapper.createUserLocationForNewest(bnsUserLocate);
    }

    @Override
    public BnsUserNewestLocate selectUserLocationForNewest(Map paramMap) {
        return bnsUserNewestLocateMapper.selectUserLocationForNewest(paramMap);
    }

    @Override
    public void updateUserLocationForNewest(BnsUserLocate bnsUserLocate) {
        bnsUserNewestLocateMapper.updateUserLocationForNewest(bnsUserLocate);
    }

    @Override
    public List<LinkedHashMap> selectUserTrackList(Map paramMap) {
        return bnsUserLocateMapper.selectUserTrackList(paramMap);
    }

    @Override
    public int createArea(BnsArea bnsArea) {
        return bnsAreaMapper.createArea(bnsArea);
    }

    @Override
    public List<BnsArea> selectArea(Map paramMap) {
        return bnsAreaMapper.selectArea(paramMap);
    }

    @Override
    public void deleteArea(int id) {
        bnsAreaMapper.deleteArea(id);
    }

    @Override
    public int createAreaPoint(BnsAreaPoint bnsAreaPoint) {
        return bnsAreaPointMapper.createAreaPoint(bnsAreaPoint);
    }

    @Override
    public List<BnsAreaPoint> selectAreaPoint(Map paramMap) {
        return bnsAreaPointMapper.selectAreaPoint(paramMap);
    }

    @Override
    public void deleteAreaPoint(int areaId) {
        bnsAreaPointMapper.deleteAreaPoint(areaId);
    }

    @Override
    public void updateArea(BnsArea bnsArea) {
        bnsAreaMapper.updateArea(bnsArea);
    }

    @Override
    public BnsArea selectAreaById(int id) {
        return bnsAreaMapper.selectAreaById(id);
    }
}
