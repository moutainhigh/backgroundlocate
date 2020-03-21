package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.BackgroundLocateUser;
import com.backGroundLocate.entity.CarInfo;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Autowired
    private CarInfoService carInfoService;

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

//    @RequestMapping(value = "/selectUserOrCarLocationForNewest")
//    public JSONObject selectUserOrCarLocationForNewest(@RequestParam(value = "user_id") String user_id,@RequestParam(value = "ids") String ids,@RequestParam(value = "type") String type){
//        System.out.println("======into selectUserOrCarLocationForNewest======");
//        JSONObject resultJson = new JSONObject();
//        JSONObject resultData = new JSONObject();
//        JSONArray locationInfo = new JSONArray();
//        try {
//
//            if(!StringUtils.isEmpty(ids)){
//                if("0".equals(type)){
//                    String[] paramIds = ids.split(",");
//                    for (int i=0;i<paramIds.length;i++){
//                        Map map = backgroundLocateUserNewestService.selectUserLocationForNewest(Integer.valueOf(paramIds[i]));
//                        locationInfo.add(map);
//                    }
//                }else if("1".equals(type)){
//                    String[] paramIds = ids.split(",");
//                    for (int i=0;i<paramIds.length;i++){
//                        Map map = backgroundLocateCarNewestService.selectCarLocationForNewest(Integer.valueOf(paramIds[i]));
//                        locationInfo.add(map);
//                    }
//                }
//            }else if(!StringUtils.isEmpty(user_id)){
//                UserInfo itemUser = userInfoService.selectUserById(Integer.valueOf(user_id));
//                if(itemUser.getLevel()==2){
//                    if("0".equals(type)){
//                        List<UserInfo> userInfoList = userInfoService.selectUserListByInternalId(itemUser.getInternalId());
//                        for(UserInfo i : userInfoList){
//                            Map userMap = backgroundLocateUserNewestService.selectUserLocationForNewest(i.getId());
//                            locationInfo.add(userMap);
//                        }
//                    }else if("1".equals(type)){
//                        List<CarInfo> carInfoList = carInfoService.selectCarListByInternalId(itemUser.getInternalId());
//                        for(CarInfo i : carInfoList){
//                            Map carMap = backgroundLocateCarNewestService.selectCarLocationForNewest(i.getId());
//                            locationInfo.add(carMap);
//                        }
//                    }
//                }else if(itemUser.getLevel()==3){
//                    if("0".equals(type)){
//                        List<UserInfo> userInfoList = userInfoService.selectUserListByDept(itemUser.getDept());
//                        for(UserInfo i : userInfoList){
//                            Map userMap = backgroundLocateUserNewestService.selectUserLocationForNewest(i.getId());
//                            locationInfo.add(userMap);
//                        }
//                    }else if("1".equals(type)){
//                        List<CarInfo> carInfoList = carInfoService.selectCarListByDept(itemUser.getDept());
//                        for(CarInfo i : carInfoList){
//                            Map carMap = backgroundLocateCarNewestService.selectCarLocationForNewest(i.getId());
//                            locationInfo.add(carMap);
//                        }
//                    }
//                }
//            }
//
//            resultJson.put("resultCode",0);
//            resultData.put("locationInfo",locationInfo);
//            resultJson.put("resultData",resultData);
//        }catch (Exception e){
//            e.printStackTrace();
//            resultJson.put("resultCode",1);
//            resultJson.put("resultMessage",e.getMessage());
//        }
//        System.out.println("resultJson======>"+resultJson);
//        return resultJson;
//    }
}