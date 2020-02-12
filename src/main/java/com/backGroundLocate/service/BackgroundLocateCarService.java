package com.backGroundLocate.service;

import com.backGroundLocate.entity.BackgroundLocateCar;
import com.backGroundLocate.entity.BackgroundLocateUser;

public interface BackgroundLocateCarService {

    void saveLocation(BackgroundLocateCar backgroundLocateCar);

    void deleteLocation(int id);
}
