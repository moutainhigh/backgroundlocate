package com.backGroundLocate.service;

import com.backGroundLocate.entity.CarInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarInfoService {

    List<CarInfo> selectCarListByInternalId(int internalId);

    List<CarInfo> selectCarListByDept(int dept);
}
