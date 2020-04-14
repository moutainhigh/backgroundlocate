package com.backGroundLocate.service;

import com.backGroundLocate.entity.AuthAccount;
import com.backGroundLocate.entity.AuthRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuthService {
    int createRole(AuthRole authRole);

    List<AuthRole> selectRole(Map paramMap);

    AuthRole selectRoleById(int id);

    void updateRole(AuthRole authRole);

    void deleteRole(int id);

    int createAccount(AuthAccount authAccount);

    List<AuthAccount> selectAccount(Map paramMap);

    AuthAccount selectAccountById(int id);

    void updateAccount(AuthAccount authAccount);

    void deleteAccount(int id);

    AuthAccount selectByAccount(String account);

    AuthAccount selectAccountByUser(@Param("userId") int userId);

}
