package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class InsVehicle {
    private int id;
    private String vehicleName;
    private int typeId;
    private String typeName;
    private int deptId;
    private String deptName;
    private String simNumber;
    private long annual;
    private String maintenance;
    private long createTime;
}
