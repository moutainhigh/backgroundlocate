package com.backGroundLocate.service;

import com.backGroundLocate.entity.BackgroundLocate;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface BackgroundLocateNewestService {
    void saveLocationOfNewest(BackgroundLocate backgroundLocate);

    void deleteLocationOfNewest(BackgroundLocate backgroundLocate);

    Map selectUserLocationForNewest(int id);
}
