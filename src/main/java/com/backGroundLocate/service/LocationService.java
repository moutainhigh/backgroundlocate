package com.backGroundLocate.service;

import com.backGroundLocate.entity.BnsIllegal;
import com.backGroundLocate.entity.BnsUserLocate;
import com.backGroundLocate.entity.BnsUserNewestLocate;

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
}
