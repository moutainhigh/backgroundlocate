package com.backGroundLocate.entity;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {

    private int id;
    private String userName;
    private String password;
    private String name;
    private int level;
    private int deptId;
    private String deptName;
    private int leaderId;

}
