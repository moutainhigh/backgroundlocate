package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.BackgroundLocateCar;
import com.backGroundLocate.mapper.BackgroundLocateCarMapper;
import com.backGroundLocate.service.BackgroundLocateCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackgroundLocateCarServiceImpl implements BackgroundLocateCarService {

    @Autowired
    private BackgroundLocateCarMapper backgroundLocateCarMapper;

    @Override
    public void saveLocation(BackgroundLocateCar backgroundLocateCar) {
        backgroundLocateCarMapper.saveLocation(backgroundLocateCar);
    }

    @Override
    public void deleteLocation(int id) {
        backgroundLocateCarMapper.deleteLocation(id);
    }
}
