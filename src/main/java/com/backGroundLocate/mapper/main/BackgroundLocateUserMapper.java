package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.BackgroundLocateUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface BackgroundLocateUserMapper {

    void saveLocation(BackgroundLocateUser backgroundLocateUser);

    void deleteLocation(@Param("id") int id);
}
