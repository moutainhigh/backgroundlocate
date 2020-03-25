package com.backGroundLocate.entity;

import lombok.Data;

import java.util.List;

@Data
public class BackgroundLocateUserNewest {

    private int id;
    private int user_id;
    private String address;
    private String longitude;
    private String latitude;
    private String longitude_latitude;
    private String upload_time;
    private String times_tamp;
    private List empIds;
}
