package com.backGroundLocate.service;

import com.backGroundLocate.entity.BackgroundLocateUser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface BackgroundLocateUserService {

    void saveLocation(BackgroundLocateUser backgroundLocateUser);

    void deleteLocation(int id);

    Map selectUserTrack(Map paramMap);

    List<LinkedHashMap> selectUserTrackList(Map paramMap);
}
