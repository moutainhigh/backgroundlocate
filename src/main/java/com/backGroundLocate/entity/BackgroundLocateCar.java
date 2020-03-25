package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class BackgroundLocateCar {

    private int id;
    private int carId;
    private String carName;
    private String address;
    private String longitude;
    private String latitude;
    private String longitude_latitude;
    private String upload_time;
    private String times_tamp;
}
