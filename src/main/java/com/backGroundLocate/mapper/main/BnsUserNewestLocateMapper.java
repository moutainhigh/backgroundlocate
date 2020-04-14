package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.BnsUserLocate;
import com.backGroundLocate.entity.BnsUserNewestLocate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface BnsUserNewestLocateMapper {

    void createUserLocationForNewest(BnsUserLocate bnsUserLocate);

    BnsUserNewestLocate selectUserLocationForNewest(Map paramMap);

    void updateUserLocationForNewest(BnsUserLocate bnsUserLocate);

}
