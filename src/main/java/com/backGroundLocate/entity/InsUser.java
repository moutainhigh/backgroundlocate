package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class InsUser {

    private int id;
    private String userName;
    private String userAccount;
    private int roleId;
    private String roleName;
    private int deptId;
    private String deptName;
    private int foremanId;
    private String foremanName;
    private String phoneNumber;
    private int createTime;
}
