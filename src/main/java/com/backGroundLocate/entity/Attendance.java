package com.backGroundLocate.entity;

import lombok.Data;

import java.util.Date;


@Data
public class Attendance {

    private int id;
    private int userId;
    private String userName;
    private Date attendanceTime;
    private String lonlat;
    private String address;
    private int state;
    private int type;
    private String remark;
}
