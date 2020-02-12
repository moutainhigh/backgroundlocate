package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.BackgroundLocateUser;
import com.backGroundLocate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
public class BackgroundLocateController {
    @Autowired
    private BackgroundLocateUserService backgroundLocateUserService;

    @Autowired
    private BackgroundLocateUserNewestService backgroundLocateUserNewestService;

    @Autowired
    private BackgroundLocateCarService backgroundLocateCarService;

    @Autowired
    private BackgroundLocateCarNewestService backgroundLocateCarNewestService;


    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping(value = "/saveLocation")
    public JSONObject saveLocation(BackgroundLocateUser backgroundLocateUser){
        System.out.println("======into saveLocation======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(Long.valueOf(backgroundLocateUser.getTimes_tamp())*1000);
            backgroundLocateUser.setUpload_time(simpleDateFormat.format(date));
            backgroundLocateUserService.saveLocation(backgroundLocateUser);
            backgroundLocateUserNewestService.deleteLocationOfNewest(backgroundLocateUser);
            backgroundLocateUserNewestService.saveLocationOfNewest(backgroundLocateUser);
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

    @RequestMapping(value = "/selectUserOrCarLocationForNewest")
    public JSONObject selectUserOrCarLocationForNewest(@RequestParam(value = "ids") String ids,@RequestParam(value = "type") String type){
        System.out.println("======into selectUserOrCarLocationForNewest======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        JSONArray locationInfo = new JSONArray();
        try {
            if("0".equals(type)){
                if(ids.contains(",")){
                    String[] paramIds = ids.split(",");
                    for (int i=0;i<paramIds.length;i++){
                        Map map = backgroundLocateUserNewestService.selectUserLocationForNewest(Integer.valueOf(paramIds[i]));
                        locationInfo.add(map);
                    }
                }else{
                    Map map = backgroundLocateUserNewestService.selectUserLocationForNewest(Integer.valueOf(ids));
                    locationInfo.add(map);
                }

            }else if("1".equals(type)){
                if(ids.contains(",")){
                    String[] paramIds = ids.split(",");
                    for (int i=0;i<paramIds.length;i++){
                        Map map = backgroundLocateCarNewestService.selectCarLocationForNewest(Integer.valueOf(paramIds[i]));
                        locationInfo.add(map);
                    }
                }else{
                    Map map = backgroundLocateCarNewestService.selectCarLocationForNewest(Integer.valueOf(ids));
                    locationInfo.add(map);
                }
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

    /*@RequestMapping(value = "/selectUserLocationForNewest")
    public JSONObject selectUserLocationForNewest(@RequestParam(value = "empIds") List<String> empIds){
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        try {
            for(int i = 0;i < empIds.size();i++){
                System.out.println("emp["+i+"]==="+empIds.get(i));
            }
            System.out.println("======into selectUserLocationForNewest======");
            JSONArray locationInfo = new JSONArray();
            for (int i=0;i<empIds.size();i++){
                Map map = backgroundLocateUserNewestService.selectUserLocationForNewest(Integer.valueOf(empIds.get(i)));
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
    }*/
}