package com.backGroundLocate.service;

import com.backGroundLocate.entity.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface LocationService {
    int createIllegal(BnsIllegal bnsIllegal);

    List<BnsIllegal> selectIllegal(Map paramMap);

    BnsIllegal selectIllegalById(int id);

    int selectIllegalNum(Map paramMap);

    void createUserLocation(BnsUserLocate bnsUserLocate);

    void createUserLocationForNewest(BnsUserLocate bnsUserLocate);

    BnsUserNewestLocate selectUserLocationForNewest(Map paramMap);

    void updateUserLocationForNewest(BnsUserLocate bnsUserLocate);

    List<LinkedHashMap> selectUserTrackList(Map paramMap);

    int createArea(BnsArea bnsArea);

    List<BnsArea> selectArea(Map paramMap);

    void deleteArea(int id);

    int createAreaPoint(BnsAreaPoint bnsAreaPoint);

    List<BnsAreaPoint> selectAreaPoint(Map paramMap);

    void deleteAreaPoint(int areaId);

    void updateArea(BnsArea bnsArea);

    BnsArea selectAreaById(int id);
}
