package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.BnsIllegal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface BnsIllegalMapper {

    int createIllegal(BnsIllegal bnsIllegal);

    List<BnsIllegal> selectIllegal(Map paramMap);

    BnsIllegal selectIllegalById(@Param("id") int id);
    
    int selectIllegalNum(Map paramMap);
}
