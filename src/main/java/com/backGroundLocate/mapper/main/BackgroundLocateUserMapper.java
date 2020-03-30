package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.BackgroundLocateUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface BackgroundLocateUserMapper {

    void saveLocation(BackgroundLocateUser backgroundLocateUser);

    void deleteLocation(@Param("id") int id);

    Map selectUserTrack(Map paramMap);

    List<LinkedHashMap> selectUserTrackList(Map paramMap);
}
