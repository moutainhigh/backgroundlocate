package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.BackgroundLocateCar;
import com.backGroundLocate.mapper.main.BackgroundLocateCarNewestMapper;
import com.backGroundLocate.service.BackgroundLocateCarNewestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BackgroundLocateCarNewestServiceImpl implements BackgroundLocateCarNewestService {

    @Autowired
    private BackgroundLocateCarNewestMapper backgroundLocateCarNewestMapper;

    @Override
    public void saveLocationOfNewest(BackgroundLocateCar backgroundLocateCar) {
        backgroundLocateCarNewestMapper.saveLocationOfNewest(backgroundLocateCar);
    }

    @Override
    public void deleteLocationOfNewest(BackgroundLocateCar backgroundLocateCar) {
        backgroundLocateCarNewestMapper.deleteLocationOfNewest(backgroundLocateCar);
    }

    @Override
    public Map selectCarLocationForNewest(int id) {
        return backgroundLocateCarNewestMapper.selectCarLocationForNewest(id);
    }
}
