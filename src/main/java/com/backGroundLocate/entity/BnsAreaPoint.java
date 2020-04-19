package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class BnsAreaPoint {
    private int id;
    private int areaId;
    private String areaName;
    private String longitude;
    private String latitude;
}
