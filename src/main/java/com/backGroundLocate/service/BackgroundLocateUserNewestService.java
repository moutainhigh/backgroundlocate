package com.backGroundLocate.service;

import com.backGroundLocate.entity.BackgroundLocateUser;

import java.util.Map;

public interface BackgroundLocateUserNewestService {
    void saveLocationOfNewest(BackgroundLocateUser backgroundLocateUser);

    void deleteLocationOfNewest(BackgroundLocateUser backgroundLocateUser);

    Map selectUserLocationForNewest(int id);
}