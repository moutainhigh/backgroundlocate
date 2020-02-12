package com.backGroundLocate.entity;

import java.util.List;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public String getCar_name() {
        return car_name;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude_latitude() {
        return longitude_latitude;
    }

    public void setLongitude_latitude(String longitude_latitude) {
        this.longitude_latitude = longitude_latitude;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public String getTimes_tamp() {
        return times_tamp;
    }

    public void setTimes_tamp(String times_tamp) {
        this.times_tamp = times_tamp;
    }
}
