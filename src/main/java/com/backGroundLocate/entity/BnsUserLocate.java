package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class BnsUserLocate {

    private int id;
    private int userId;
    private String address;
    private String longitude;
    private String latitude;
    private String longitudeLatitude;
    private String uploadTime;
    private String timestamp;
}
