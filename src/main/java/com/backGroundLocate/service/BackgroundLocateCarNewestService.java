package com.backGroundLocate.service;

import com.backGroundLocate.entity.BackgroundLocateCar;

import java.util.Map;

public interface BackgroundLocateCarNewestService {
    void saveLocationOfNewest(BackgroundLocateCar backgroundLocateCar);

    void deleteLocationOfNewest(BackgroundLocateCar backgroundLocateCar);

    Map selectCarLocationForNewest(int id);
}
