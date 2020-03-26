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
        Map paramMap = new HashMap<String, String>();
        Department conDept;
        try {

            UserInfo userInfo = userInfoService.selectUserById(Integer.valueOf(userId));
            Department userDept = departmentService.selectDepartmentByPrimary(userInfo.getDeptId());
            paramMap.put("userLevel",userInfo.getLevel());

            if(userInfo.getLevel()>=3){
                if(!StringUtils.isEmpty(deptName)){
                    conDept = new Department();
                    conDept.setDeptName(deptName);
                    Department itemDept = departmentService.selectDepartment(conDept);
                    if(itemDept != null){
                        if(itemDept.getId() != userDept.getId()) {
                            //三级账号,除本部门外其余同级部门与上级部门无权查询
                            resultJson.put("resultCode", 1);
                            resultJson.put("resultMessage", "权限不足,无法查询");
                            return resultJson;
                        }
                    }else{
                        resultJson.put("resultCode", 1);
                        resultJson.put("resultMessage", "未查询到部门");
                        return resultJson;
                    }
                }else if(!StringUtils.isEmpty(unitName)){
                    paramMap.put("unitName",unitName);
                }
                paramMap.put("deptId",userDept.getId());
                List<Map> mapList = backgroundLocateUserNewestService.selectUserLocationForNewest(paramMap);
                resultData.put("unitLocationList",mapList);
            }else if(userInfo.getLevel()==2){
                if(!StringUtils.isEmpty(deptName)){
                    conDept = new Department();
                    conDept.setDeptName(deptName);
                    Department itemDept = departmentService.selectDepartment(conDept);
                    if(itemDept != null){
                        if(itemDept.getDeptLevel()>=userDept.getDeptLevel()) {
                            //二级账号,除本部门与下属部门外其余同级部门与上级部门无权查询
                            if (itemDept.getParentId() == userDept.getId()) {
                                //目标部门的上级部门id与账号所属部门id相同，查询目标部门下所属人员信息
                                paramMap.put("onlyDeptName",deptName);
                            }else if(itemDept.getId() == userDept.getId()){
                                //目标部门id与账号所属部门id相同，查询目标部门及下属所有部门人员信息
                                paramMap.put("deptName",deptName);
                                paramMap.put("deptId",userDept.getId());
                                paramMap.put("parentId",userDept.getId());
                            }else{
                                resultJson.put("resultCode", 1);
                                resultJson.put("resultMessage", "权限不足,无法查询");
                                return resultJson;
                            }
                        }else{
                            resultJson.put("resultCode", 1);
                            resultJson.put("resultMessage", "权限不足,无法查询");
                            return resultJson;
                        }
                    }else{
                        resultJson.put("resultCode", 1);
                        resultJson.put("resultMessage", "未查询到部门");
                        return resultJson;
                    }
                }else if(!StringUtils.isEmpty(unitName)){
                    paramMap.put("unitName",unitName);
                    paramMap.put("deptId",userDept.getId());
                    paramMap.put("parentId",userDept.getId());
                }else{
                    paramMap.put("deptName",userDept.getDeptName());
                    paramMap.put("deptId",userDept.getId());
                    paramMap.put("parentId",userDept.getId());
                }
                List<Map> mapList = backgroundLocateUserNewestService.selectUserLocationForNewest(paramMap);
                resultData.put("unitLocationList",mapList);

            }else if(userInfo.getLevel()==1){
                if(!StringUtils.isEmpty(deptName)){
                    conDept = new Department();
                    conDept.setDeptName(deptName);
                    Department itemDept = departmentService.selectDepartment(conDept);
                    if(itemDept != null){
                        if(itemDept.getDeptLevel()==2){
                            //如果目标部门为2级部门,查询目标部门及下属所有部门人员信息
                            paramMap.put("deptName",itemDept.getDeptName());
                            paramMap.put("parentId",itemDept.getId());
                        }else if(itemDept.getDeptLevel()==3){
                            //如果目标部门为3级部门,查询目标部门下属所有人员信息
                            paramMap.put("onlyDeptName",itemDept.getDeptName());
                        }
                    }else {
                        resultJson.put("resultCode", 1);
                        resultJson.put("resultMessage", "未查询到部门");
                        return resultJson;
                    }

                }else if(!StringUtils.isEmpty(unitName)){
                    paramMap.put("unitName",unitName);
                }
                List<Map> mapList = backgroundLocateUserNewestService.selectUserLocationForNewest(paramMap);
                resultData.put("unitLocationList",mapList);
            }

            resultJson.put("resultCode",0);
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