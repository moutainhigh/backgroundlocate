package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.mapper.UserInfoMapper;
import com.backGroundLocate.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo selectUser(UserInfo userInfo) {
        return userInfoMapper.selectUser(userInfo);
    }

    @Override
    public List<UserInfo> selectUserListByInternalId(int internalId) {
        return userInfoMapper.selectUserListByInternalId(internalId);
    }
}
