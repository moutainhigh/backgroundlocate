package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class AuthRole {

    private int roleId;
    private String roleName;
    private int roleLevel;
    private int createTime;
}
