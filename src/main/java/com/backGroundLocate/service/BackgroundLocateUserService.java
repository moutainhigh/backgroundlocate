package com.backGroundLocate.service;

import com.backGroundLocate.entity.BackgroundLocateUser;

public interface BackgroundLocateUserService {

    void saveLocation(BackgroundLocateUser backgroundLocateUser);

    void deleteLocation(int id);
}
