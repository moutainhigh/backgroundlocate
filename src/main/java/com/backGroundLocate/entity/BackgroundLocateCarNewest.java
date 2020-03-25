package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class BackgroundLocateCarNewest {

    private int id;
    private int car_id;
    private String car_name;
    private String address;
    private String longitude;
    private String latitude;
    private String longitude_latitude;
    private String upload_time;
    private String times_tamp;
}
