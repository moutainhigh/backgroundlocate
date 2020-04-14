package com.backGroundLocate.service;

import com.backGroundLocate.entity.InsUser;

import java.util.List;
import java.util.Map;

public interface UserService {

    int createUser(InsUser insUser);

    List<InsUser> selectUser(Map paramMap);

    InsUser selectUserById(int id);

    void updateUser(InsUser insUser);

    void deleteUser(int id);

    List<InsUser> selectDirectlyUser(int deptId);
}
