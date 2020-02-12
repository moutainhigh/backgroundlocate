package com.backGroundLocate.entity;

import java.util.List;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public List getEmpIds() {
        return empIds;
    }

    public void setEmpIds(List empIds) {
        this.empIds = empIds;
    }
}
