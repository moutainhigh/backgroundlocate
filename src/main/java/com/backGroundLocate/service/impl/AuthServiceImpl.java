package com.backGroundLocate.service.impl;

import com.backGroundLocate.mapper.main.AuthAccountMapper;
import com.backGroundLocate.mapper.main.AuthRoleMapper;
import com.backGroundLocate.entity.AuthAccount;
import com.backGroundLocate.entity.AuthRole;
import com.backGroundLocate.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthRoleMapper authRoleMapper;

    @Autowired
    private AuthAccountMapper authAccountMapper;

    @Override
    public int createRole(AuthRole authRole) {
       return authRoleMapper.createRole(authRole);
    }

    @Override
    public List<AuthRole> selectRole(Map paramMap) {
        return authRoleMapper.selectRole(paramMap);
    }

    @Override
    public AuthRole selectRoleById(int id) {
        return authRoleMapper.selectRoleById(id);
    }

    @Override
    public void updateRole(AuthRole authRole) {
        authRoleMapper.updateRole(authRole);
    }

    @Override
    public void deleteRole(int id) {
        authRoleMapper.deleteRole(id);
    }

    @Override
    public int createAccount(AuthAccount authAccount) {
        return authAccountMapper.createAccount(authAccount);
    }

    @Override
    public List<AuthAccount> selectAccount(Map paramMap) {
        return authAccountMapper.selectAccount(paramMap);
    }

    @Override
    public AuthAccount selectAccountById(int id) {
        return authAccountMapper.selectAccountById(id);
    }

    @Override
    public void updateAccount(AuthAccount authAccount) {
        authAccountMapper.updateAccount(authAccount);
    }

    @Override
    public void deleteAccount(int id) {
        authRoleMapper.deleteRole(id);
    }

    @Override
    public AuthAccount selectByAccount(String account) {
        return authAccountMapper.selectByAccount(account);
    }

    @Override
    public AuthAccount selectAccountByUser(int userId) {
        return authAccountMapper.selectAccountByUser(userId);
    }
}
