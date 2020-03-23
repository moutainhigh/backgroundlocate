package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.BackgroundLocateUser;
import com.backGroundLocate.mapper.main.BackgroundLocateUserNewestMapper;
import com.backGroundLocate.service.BackgroundLocateUserNewestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BackgroundLocateUserNewestServiceImpl implements BackgroundLocateUserNewestService {

    @Autowired
    private BackgroundLocateUserNewestMapper backgroundLocateNewestMapper;

    @Override
    public void saveLocationOfNewest(BackgroundLocateUser backgroundLocateUser) {
        backgroundLocateNewestMapper.saveLocationOfNewest(backgroundLocateUser);
    }

    @Override
    public void deleteLocationOfNewest(BackgroundLocateUser backgroundLocateUser) {
        backgroundLocateNewestMapper.deleteLocationOfNewest(backgroundLocateUser);
    }

    @Override
    public Map selectUserLocationForNewest(int id) {
        return backgroundLocateNewestMapper.selectUserLocationForNewest(id);
    }
}
