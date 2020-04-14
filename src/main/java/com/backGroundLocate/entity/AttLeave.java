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
    private int approvalState;
    private long approvalTime;
    private String approvalOpinion;
    private String remark;
}
