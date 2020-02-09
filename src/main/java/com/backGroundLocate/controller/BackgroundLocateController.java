package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.BackgroundLocate;
import com.backGroundLocate.entity.BackgroundLocateNewest;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.BackgroundLocateNewestService;
import com.backGroundLocate.service.BackgroundLocateService;
import com.backGroundLocate.service.UserInfoService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class BackgroundLocateController {
    @Autowired
    private BackgroundLocateService backgroundLocateService;

    @Autowired
    private BackgroundLocateNewestService backgroundLocateNewestService;

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping(value = "/saveLocation")
    public JSONObject saveLocation(BackgroundLocate backgroundLocate){
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        try {
            System.out.println("======into saveLocation======");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(Long.valueOf(backgroundLocate.getTimes_tamp())*1000);
            backgroundLocate.setUpload_time(simpleDateFormat.format(date));
            backgroundLocateService.saveLocation(backgroundLocate);
            backgroundLocateNewestService.deleteLocationOfNewest(backgroundLocate);
            backgroundLocateNewestService.saveLocationOfNewest(backgroundLocate);
            resultJson.put("resultCode",0);
            resultData.put("resultStatus","success");
            resultJson.put("resultData",resultData);
        }catch (Exception e){
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        System.out.println("resultJson======>"+resultJson);
        return resultJson;
    }

    @RequestMapping(value = "/selectUserLocationForNewest")
    public JSONObject selectUserLocationForNewest(@RequestParam(value = "empIds") List<Integer> empIds){
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        try {
            System.out.println("======into selectUserLocationForNewest======");
            JSONArray locationInfo = new JSONArray();
            for (int i=0;i<empIds.size();i++){
                Map map = backgroundLocateNewestService.selectUserLocationForNewest(empIds.get(i));
                locationInfo.add(map);
            }
            resultJson.put("resultCode",0);
            resultData.put("locationInfo",locationInfo);
            resultJson.put("resultData",resultData);
        }catch (Exception e){
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        System.out.println("resultJson======>"+resultJson);
        return resultJson;
    }
}