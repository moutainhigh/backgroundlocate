package com.backGroundLocate.mapper;

import com.backGroundLocate.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserInfoMapper {

    UserInfo selectUser(UserInfo userInfo);

    UserInfo selectUserById(@Param("id") int id);


    List<UserInfo> selectUserListByInternalId(@Param("internalId") int internalId);

    List<UserInfo> selectUserListByDept(@Param("dept") int dept);
}
