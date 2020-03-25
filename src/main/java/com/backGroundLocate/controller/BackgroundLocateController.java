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

    @RequestMapping(value = "/selectUnitLocationForNewest")
    public JSONObject selectUnitLocationForNewest(@RequestParam(value = "userId") String userId,
                                                  @RequestParam(value = "deptName",required = false) String deptName,
                                                  @RequestParam(value = "unitName",required = false) String unitName,
                                                  @RequestParam(value = "type",required = false) String type){
        System.out.println("======into selectUnitLocationForNewest======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        JSONObject unitLocation = new JSONObject();
        JSONArray unitLocationList = new JSONArray();
        Map paramMap = new HashMap<String, String>();
        Department conDept;
        UserInfo conUser;
        try {
            UserInfo userInfo = userInfoService.selectUserById(Integer.valueOf(userId));
            if (!StringUtils.isEmpty(deptName)) {
                if(userInfo.getLevel()>2){
                    resultJson.put("resultCode",1);
                    resultJson.put("resultMessage","权限不足,无法查询部门信息");
                    return resultJson;
                }else if(userInfo.getLevel()==2){
                    Department dept = departmentService.selectDepartmentByPrimary(userInfo.getDeptId());
                    paramMap.put("deptName",deptName);
                    paramMap.put("deptId",dept.getId());
                    paramMap.put("parentId",dept.getId());
                    List<Map> mapList = backgroundLocateUserNewestService.selectUserLocationForNewest(paramMap);
                    resultJson.put("unitLocationList",mapList);
                }
            }else if(!StringUtils.isEmpty(unitName)){
                paramMap.put("unitName",unitName);
                List<Map> mapList = backgroundLocateUserNewestService.selectUserLocationForNewest(paramMap);
                resultJson.put("unitLocation",mapList.get(0));
            }
        }catch (Exception e){
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }

        System.out.println("resultJson======>"+resultJson);
        return resultJson;
    }
}