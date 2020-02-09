package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.BackgroundLocate;
import com.backGroundLocate.mapper.BackgroundLocateNewestMapper;
import com.backGroundLocate.service.BackgroundLocateNewestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BackgroundLocateNewestServiceImpl implements BackgroundLocateNewestService {

    @Autowired
    private BackgroundLocateNewestMapper backgroundLocateNewestMapper;

    @Override
    public void saveLocationOfNewest(BackgroundLocate backgroundLocate) {
        backgroundLocateNewestMapper.saveLocationOfNewest(backgroundLocate);
    }

    @Override
    public void deleteLocationOfNewest(BackgroundLocate backgroundLocate) {
        backgroundLocateNewestMapper.deleteLocationOfNewest(backgroundLocate);
    }

    @Override
    public Map selectUserLocationForNewest(int id) {
        return backgroundLocateNewestMapper.selectUserLocationForNewest(id);
    }
}
