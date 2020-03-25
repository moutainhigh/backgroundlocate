package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.CarInfo;
import com.backGroundLocate.entity.Department;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.CarInfoService;
import com.backGroundLocate.service.DepartmentService;
import com.backGroundLocate.service.UserInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "部门接口")
@RestController
public class DepartmentController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/selectUnitInfo")
    public JSONObject selectUnitInfo(@RequestParam(value = "userId") String userId,
                                     @RequestParam(value = "deptId",required = false) String deptId,
                                     @RequestParam(value = "unitId",required = false) String unitId,
                                     @RequestParam(value = "type",required = false) String type){
        System.out.println("======into selectUnitInfo======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        JSONObject information = new JSONObject();
        JSONArray deptList = new JSONArray();
        JSONArray unitList = new JSONArray();
        JSONObject departmentInfo = new JSONObject();
        Department conDept;
        UserInfo conUser;
        try {
            if(!StringUtils.isEmpty(deptId)){
                conDept = new Department();
                conDept.setParentId(Integer.valueOf(deptId));
                List<Department> departmentList = departmentService.selectDepartmentList(conDept);
                for (Department dept : departmentList){
                    List<UserInfo> userInfoList = userInfoService.selectUserListByDept(dept.getId());
                    for(UserInfo userInfo : userInfoList){
                        unitList.add(userInfo);
                    }
                    List<CarInfo> carInfoList = carInfoService.selectCarListByDept(dept.getId());
                    for(CarInfo carInfo : carInfoList){
                        unitList.add(carInfo);
                    }
                    deptList.add(dept);
                }
                departmentInfo.put("deptList",deptList);
                departmentInfo.put("unitList",unitList);
                resultData.put("departmentInfo",departmentInfo);
            }else if(!StringUtils.isEmpty(unitId)){
                if ("1".equals(type)){
                    information.put("id","");
                    information.put("name","");
                    information.put("mobile","");
                    information.put("shift","");
                    information.put("attendance","");
                    information.put("address","");
                    information.put("status","");
                    information.put("mileage","");
                    information.put("arriveTime","");
                    information.put("leaveTime","");
                }else if("2".equals(type)){
                    information.put("id","");
                    information.put("name","");
                    information.put("type","");
                    information.put("address","");
                    information.put("status","");
                    information.put("mileage","");
                }
                resultData.put("information",information);
            }else{
                UserInfo userInfo = userInfoService.selectUserById(Integer.valueOf(userId));
                List<UserInfo> userInfoList = userInfoService.selectUserListByDept(userInfo.getDeptId());
                Department dept = departmentService.selectDepartmentByPrimary(userInfo.getDeptId());
                deptList.add(dept);
                departmentInfo.put("deptList",deptList);
                departmentInfo.put("unitList",userInfoList);
                resultData.put("departmentInfo",departmentInfo);
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