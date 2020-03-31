package com.backGroundLocate.service;

import com.backGroundLocate.entity.BackgroundLocateUser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface BackgroundLocateUserService {

    void saveLocation(BackgroundLocateUser backgroundLocateUser);

    void deleteLocation(int id);

    List<LinkedHashMap> selectUserTrackList(Map paramMap);
}
