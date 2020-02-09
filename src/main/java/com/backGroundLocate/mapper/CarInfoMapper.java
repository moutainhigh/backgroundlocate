package com.backGroundLocate.mapper;

import com.backGroundLocate.entity.CarInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CarInfoMapper {

    List<CarInfo> selectCarListByInternalId(@Param("internalId") int internalId);
}
