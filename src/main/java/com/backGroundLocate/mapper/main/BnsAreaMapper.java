package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.BnsArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface BnsAreaMapper {

    int createArea(BnsArea bnsArea);

    List<BnsArea> selectArea(Map paramMap);

    void deleteArea(@Param(value = "id") int id);

    void updateArea(BnsArea bnsArea);

    BnsArea selectAreaById(@Param(value = "id") int id);
}
