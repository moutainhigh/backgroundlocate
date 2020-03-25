package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class Department {

    private int id;
    private String deptName;
    private int deptType;
    private int deptLevel;
    private int parentId;
    private String parentName;

}
