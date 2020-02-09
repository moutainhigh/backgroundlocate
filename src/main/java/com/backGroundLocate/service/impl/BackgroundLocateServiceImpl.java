package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.BackgroundLocate;
import com.backGroundLocate.mapper.BackgroundLocateMapper;
import com.backGroundLocate.service.BackgroundLocateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackgroundLocateServiceImpl implements BackgroundLocateService {

    @Autowired
    private BackgroundLocateMapper backgroundLocateMapper;

    @Override
    public void saveLocation(BackgroundLocate backgroundLocate) {
        backgroundLocateMapper.saveLocation(backgroundLocate);
    }

    @Override
    public void deleteLocation(int id) {
        backgroundLocateMapper.deleteLocation(id);
    }
}
