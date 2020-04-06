package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class AuthAccount {

    private int id;
    private int userId;
    private String userName;
    private String account;
    private String password;
    private int type;
    private int roleId;
    private String roleName;
    private int createTime;
    private int lastLoginTime;

}
