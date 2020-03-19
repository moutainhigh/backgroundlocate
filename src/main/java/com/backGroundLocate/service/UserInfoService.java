package com.backGroundLocate.service;


import com.backGroundLocate.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoService {
    UserInfo selectUser(UserInfo userInfo);

    UserInfo selectUserById(int id);

    List<UserInfo> selectUserListByInternalId(int internalId);

    List<UserInfo> selectUserListByDept(int dept);
}

