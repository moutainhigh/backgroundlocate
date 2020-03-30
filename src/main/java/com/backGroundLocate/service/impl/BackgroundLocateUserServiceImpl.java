package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.BackgroundLocateUser;
import com.backGroundLocate.mapper.main.BackgroundLocateUserMapper;
import com.backGroundLocate.service.BackgroundLocateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackgroundLocateUserServiceImpl implements BackgroundLocateUserService {

    @Autowired
    private BackgroundLocateUserMapper backgroundLocateMapper;

    @Override
    public void saveLocation(BackgroundLocateUser backgroundLocateUser) {
        backgroundLocateMapper.saveLocation(backgroundLocateUser);
    }

    @Override
    public void deleteLocation(int id) {
        backgroundLocateMapper.deleteLocation(id);
    }

    @Override
    public Map selectUserTrack(Map paramMap) {
        return backgroundLocateMapper.selectUserTrack(paramMap);
    }

    @Override
    public List<LinkedHashMap> selectUserTrackList(Map paramMap) {
        return backgroundLocateMapper.selectUserTrackList(paramMap);
    }
}
