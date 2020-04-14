package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.InsUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface InsUserMapper {
    
    int createUser(InsUser insUser);

    List<InsUser> selectUser(Map paramMap);

    InsUser selectUserById(@Param("id") int id);

    void updateUser(InsUser insUser);

    void deleteUser(@Param("id") int id);

    List<InsUser> selectDirectlyUser(@Param("deptId") int deptId);
}
