package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface UserInfoMapper {

    UserInfo selectUser(UserInfo userInfo);

    UserInfo selectUserById(@Param("id") int id);

    List<UserInfo> selectUserListByDept(@Param("deptId") int deptId);

    List<UserInfo> selectUserList(UserInfo userInfo);

    int selectUserIllegalNum(Map paramMap);

    void updateUserInfo(UserInfo userInfo);
}
