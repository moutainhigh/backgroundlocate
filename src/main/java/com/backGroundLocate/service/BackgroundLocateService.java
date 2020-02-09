package com.backGroundLocate.service;

import com.backGroundLocate.entity.BackgroundLocate;

public interface BackgroundLocateService {

    void saveLocation(BackgroundLocate backgroundLocate);

    void deleteLocation(int id);
}
