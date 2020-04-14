package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class Leave {

    private int id;
    private int userId;
    private String userName;
    private int type;
    private long startTime;
    private long endTime;
    private long timestamp;
    private String approver;
    private int approverState;
    private int approverTime;
    private String remark;
}
