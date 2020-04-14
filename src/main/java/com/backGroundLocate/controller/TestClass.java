package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.util.MD5Util;
import com.backGroundLocate.util.RestTemplateUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Date;

@Api(tags = "测试接口")
@RestController
public class TestClass {
    @Autowired
    private RestTemplateUtil restTemplateUtil;

    private String exGpsApiUrl = "http://47.104.179.40:89/gpsonline/GPSAPI";

    private String exAccount = "ydhb";

    private String exPassword = "123456";

    private String uri = "http://47.104.179.40:89/gpsonline/GPSAPI";

    public static void main(String[] args) {

    }


    @PostMapping(value = "/test")
    public void restCar(){
        MultiValueMap<String, Object> vehicleMap= new LinkedMultiValueMap<>();
        vehicleMap.add("version","1");
        vehicleMap.add("method","loadHistory");
        vehicleMap.add("vid","13986108");
        vehicleMap.add("vKey","447663dece4ed3ee3075e9ad687a5787");
        vehicleMap.add("bTime","1605068776");
        vehicleMap.add("eTime","1605068776");

        String locationStr = restTemplateUtil.PostFormData(exGpsApiUrl,vehicleMap);
        System.out.println(locationStr);
        JSONObject locationJson = JSONObject.parseObject(locationStr);
        System.out.println(locationJson.toJSONString());
    }
}
