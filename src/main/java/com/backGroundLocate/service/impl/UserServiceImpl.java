package com.backGroundLocate.service.impl;

import com.backGroundLocate.mapper.main.InsUserMapper;
import com.backGroundLocate.entity.InsUser;
import com.backGroundLocate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private InsUserMapper insUserMapper;

    @Override
    public int createUser(InsUser insUser) {
        return insUserMapper.createUser(insUser);
    }

    @Override
    public List<InsUser> selectUser(Map paramMap) {
        return insUserMapper.selectUser(paramMap);
    }

    @Override
    public InsUser selectUserById(int id) {
        return insUserMapper.selectUserById(id);
    }

    @Override
    public void updateUser(InsUser insUser) {
        insUserMapper.updateUser(insUser);
    }

    @Override
    public void deleteUser(int id) {
        insUserMapper.deleteUser(id);
    }

    @Override
    public List<InsUser> selectDirectlyUser(int deptId) {
        return insUserMapper.selectDirectlyUser(deptId);
    }
}
