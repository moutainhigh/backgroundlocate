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

    List<UserInfo> selectUserListByInternalId(@Param("internalId") int internalId);
}
