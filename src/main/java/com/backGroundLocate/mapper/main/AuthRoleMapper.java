package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.AuthRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface AuthRoleMapper {

    int createRole(AuthRole authRole);

    List<AuthRole> selectRole(Map paramMap);

    AuthRole selectRoleById(@Param("id") int id);

    void updateRole(AuthRole authRole);

    void deleteRole(@Param("id") int id);
}
