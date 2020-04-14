package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class BnsUserLocate {

    private int id;
    private int userId;
    private String address;
    private String longitude;
    private String latitude;
    private String lonLat;
    private String uploadTime;
    private long timestamp;
}
