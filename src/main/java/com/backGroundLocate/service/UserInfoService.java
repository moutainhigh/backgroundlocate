package com.backGroundLocate.service;


import com.backGroundLocate.entity.UserInfo;

import java.util.List;

public interface UserInfoService {
    UserInfo selectUser(UserInfo userInfo);

    List<UserInfo> selectUserListByInternalId(int internalId);
}

