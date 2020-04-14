package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.AuthAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface AuthAccountMapper {

    int createAccount(AuthAccount authAccount);

    List<AuthAccount> selectAccount(Map paramMap);

    AuthAccount selectAccountById(@Param("id") int id);

    void updateAccount(AuthAccount authAccount);

    void deleteAccount(@Param("id") int id);

    AuthAccount selectByAccount(@Param("account") String account);

    AuthAccount selectAccountByUser(@Param("userId") int userId);
}
