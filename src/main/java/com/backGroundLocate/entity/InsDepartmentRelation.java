package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class InsDepartmentRelation {

    private int id;
    private int deptId;
    private String deptName;
    private int parentDeptId;
    private String parentDeptName;
    private long createTime;
}
