package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class AttLeave {

    private int id;
    private int userId;
    private String userName;
    private int type;
    private long startTime;
    private long endTime;
    private long timestamp;
    private int approverId;
    private String approverName;
    private int approveState;
    private long approveTime;
    private String approveOpinion;
    private String remark;
}
