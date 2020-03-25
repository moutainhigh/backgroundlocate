package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.BackgroundLocateUser;
import com.backGroundLocate.entity.CarInfo;
import com.backGroundLocate.entity.Department;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags="定位接口")
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

    @Autowired
    private DepartmentService departmentService;


    @ApiOperation(value="人员定位上传")
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
//    public JSONObject selectUserOrCarLocationForNewest(@RequestParam(value = "userId") String user_id,
//                                                       @RequestParam(value = "deptList",required = false) String deptList,
//                                                       @RequestParam(value = "idList",required = false) String idList){
//        System.out.println("======into selectUserOrCarLocationForNewest======");
//        JSONObject resultJson = new JSONObject();
//        JSONObject resultData = new JSONObject();
//        JSONArray locationInfo = new JSONArray();
//        try {
//
//            if(!StringUtils.isEmpty(projectDeptList)){
//                String[] paramIds = projectDeptList.split(",");
//                Department conDept = new Department();
//                for (String paramId:paramIds){
//                    conDept.setParentId(Integer.valueOf(paramId));
//                    conDept.setDeptType(Integer.valueOf(type));
//                    List<Department> list = departmentService.selectDepartment(conDept);
//                    for(Department dept : list){
//                        if (dept.getDeptType()==3) {
//                            List<UserInfo> userInfoList = userInfoService.selectUserListByDept(dept.getId());
//                            for (UserInfo userInfo : userInfoList) {
//                                Map map = backgroundLocateUserNewestService.selectUserLocationForNewest(userInfo.getId());
//                                locationInfo.add(map);
//                            }
//                        }else if(dept.getDeptType()==4){
//                            List<CarInfo> carInfoList = carInfoService.selectCarListByDept(dept.getId());
//                            for (CarInfo carInfo : carInfoList) {
//                                Map map = backgroundLocateCarNewestService.selectCarLocationForNewest(carInfo.getId());
//                                locationInfo.add(map);
//                            }
//                        }
//                    }
//                }
//            }else if(!StringUtils.isEmpty(deptList)){
//                if("0".equals(type)){
//                    String[] paramIds = deptList.split(",");
//                    for (int i=0;i<paramIds.length;i++){
//                        Map map = backgroundLocateUserNewestService.selectUserLocationForNewest(Integer.valueOf(paramIds[i]));
//                        locationInfo.add(map);
//                    }
//                }else if("1".equals(type)){
//                    String[] paramIds = deptList.split(",");
//                    for (int i=0;i<paramIds.length;i++){
//                        Map map = backgroundLocateCarNewestService.selectCarLocationForNewest(Integer.valueOf(paramIds[i]));
//                        locationInfo.add(map);
//                    }
//                }
//            }else if(!StringUtils.isEmpty(idList)){
//                if("0".equals(type)){
//                    String[] paramIds = idList.split(",");
//                    for (int i=0;i<paramIds.length;i++){
//                        Map map = backgroundLocateUserNewestService.selectUserLocationForNewest(Integer.valueOf(paramIds[i]));
//                        locationInfo.add(map);
//                    }
//                }else if("1".equals(type)){
//                    String[] paramIds = idList.split(",");
//                    for (int i=0;i<paramIds.length;i++){
//                        Map map = backgroundLocateCarNewestService.selectCarLocationForNewest(Integer.valueOf(paramIds[i]));
//                        locationInfo.add(map);
//                    }
//                }
//            }else if(!StringUtils.isEmpty(user_id)){
////                UserInfo itemUser = userInfoService.selectUserById(Integer.valueOf(user_id));
////                if(itemUser.getLevel()==2){
////                    if("0".equals(type)){
////                        List<UserInfo> userInfoList = userInfoService.selectUserListByInternalId(itemUser.getInternalId());
////                        for(UserInfo i : userInfoList){
////                            Map userMap = backgroundLocateUserNewestService.selectUserLocationForNewest(i.getId());
////                            locationInfo.add(userMap);
////                        }
////                    }else if("1".equals(type)){
////                        List<CarInfo> carInfoList = carInfoService.selectCarListByInternalId(itemUser.getInternalId());
////                        for(CarInfo i : carInfoList){
////                            Map carMap = backgroundLocateCarNewestService.selectCarLocationForNewest(i.getId());
////                            locationInfo.add(carMap);
////                        }
////                    }
////                }else if(itemUser.getLevel()==3){
////                    if("0".equals(type)){
////                        List<UserInfo> userInfoList = userInfoService.selectUserListByDept(itemUser.getDeptId());
////                        for(UserInfo i : userInfoList){
////                            Map userMap = backgroundLocateUserNewestService.selectUserLocationForNewest(i.getId());
////                            locationInfo.add(userMap);
////                        }
////                    }else if("1".equals(type)){
////                        List<CarInfo> carInfoList = carInfoService.selectCarListByDept(itemUser.getDeptId());
////                        for(CarInfo i : carInfoList){
////                            Map carMap = backgroundLocateCarNewestService.selectCarLocationForNewest(i.getId());
////                            locationInfo.add(carMap);
////                        }
////                    }
////                }
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