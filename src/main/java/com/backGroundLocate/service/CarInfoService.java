package com.backGroundLocate.service;

import com.backGroundLocate.entity.CarInfo;

import java.util.List;

public interface CarInfoService {

    List<CarInfo> selectCarListByInternalId(int internalId);
}
