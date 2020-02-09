package com.backGroundLocate.entity;

public class BackgroundLocate {

    private int id;
    private int userId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
