package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.BnsUserLocate;
import com.backGroundLocate.entity.BnsUserNewestLocate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface BnsUserLocateMapper {

    void createUserLocation(BnsUserLocate bnsUserLocate);

    List<LinkedHashMap> selectUserTrackList(Map paramMap);

}
