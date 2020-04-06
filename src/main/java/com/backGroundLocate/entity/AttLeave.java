package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class AttLeave {

    private int id;
    private int userId;
    private String userName;
    private int type;
    private int startTime;
    private int endTime;
    private int timestamp;
    private String approver;
    private int approverState;
    private int approverTime;
    private String remark;
}
