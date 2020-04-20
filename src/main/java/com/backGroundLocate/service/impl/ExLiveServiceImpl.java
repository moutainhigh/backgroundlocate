package com.backGroundLocate.service.impl;

import com.backGroundLocate.mapper.exlive.ExLiveMapper;
import com.backGroundLocate.service.ExLiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExLiveServiceImpl implements ExLiveService {

    @Autowired
    private ExLiveMapper exLiveMapper;

    @Override
    public Integer selectselectVehicleIdBySimNumber(String simNumber) {
        return exLiveMapper.selectVehicleIdBySimNumber(simNumber);
    }

    @Override
    public String selectVehicleLastRunDate(String simNumber) {
        return exLiveMapper.selectVehicleLastRunDate(simNumber);
    }
}
