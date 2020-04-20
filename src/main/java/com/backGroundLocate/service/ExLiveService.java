package com.backGroundLocate.service;

public interface ExLiveService {
    Integer selectselectVehicleIdBySimNumber(String simNumber);

    String selectVehicleLastRunDate(String simNumber);
}
