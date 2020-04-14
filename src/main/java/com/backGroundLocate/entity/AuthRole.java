package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class AuthRole {

    private int id;
    private String roleName;
    private int roleLevel;
    private long createTime;
}
