package com.backGroundLocate.entity;

import lombok.Data;

@Data
public class BnsIllegal {

    private int id;
    private int unitId;
    private String unitName;
    private int illegalType;
    private int illegalTime;
}
