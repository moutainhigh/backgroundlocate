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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/selectUnitInfoForFuzzy")
    public JSONObject selectUnitInfoForFuzzy(@RequestParam(value = "userId") String userId,
                                             @RequestParam(value = "type") String type,
                                             @RequestParam(value = "searchName") String searchName){
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        JSONArray resultList = new JSONArray();
        Department conDept = new Department();
        UserInfo conUser = new UserInfo();
        CarInfo conCar = new CarInfo();

        try {
            UserInfo userInfo = userInfoService.selectUserById(Integer.parseInt(userId));
            Department userDept = departmentService.selectDepartmentByPrimary(userInfo.getDeptId());

            conDept.setDeptName(searchName);
            List<Department> departmentList = departmentService.selectDepartmentList(conDept);
            conUser.setName(searchName);
            List<UserInfo> userInfoList = userInfoService.selectUserList(conUser);
            conCar.setName(searchName);
            List<CarInfo> carInfoList = carInfoService.selectCarList(conCar);

            if(userInfo.getLevel()>=3){
                for (Department department:departmentList){
                    if(department.getId()==userDept.getId()){
                        Map map = new HashMap();
                        map.put("id",department.getId());
                        map.put("name",department.getDeptName());
                        resultList.add(map);
                    }
                }
                if("1".equals(type)){
                    for (UserInfo user : userInfoList){
                        if(user.getDeptId()==userDept.getId()&&user.getName().contains(searchName)){
                            Map map = new HashMap();
                            map.put("id",user.getId());
                            map.put("name",user.getName());
                            resultList.add(map);
                        }
                    }
                }else if("2".equals(type)){
                    for (CarInfo car : carInfoList){
                        if(car.getDeptId()==userDept.getId()&&car.getName().contains(searchName)){
                            Map map = new HashMap();
                            map.put("id",car.getId());
                            map.put("name",car.getName());
                            resultList.add(map);
                        }
                    }
                }
            }else if(userInfo.getLevel()==2){
                conDept = new Department();
                conDept.setParentId(userDept.getId());
                List<Department> subDepartmentList = departmentService.selectDepartmentList(conDept);

                for (Department department:departmentList){
                    if(department.getId()==userDept.getId()){
                        Map map = new HashMap();
                        map.put("id",department.getId());
                        map.put("name",department.getDeptName());
                        map.put("type",1);
                        resultList.add(map);
                    }
                    if("1".equals(type)){
                        if(department.getParentId()==userDept.getId()&&department.getDeptType()==3){
                            Map map = new HashMap();
                            map.put("id",department.getId());
                            map.put("name",department.getDeptName());
                            map.put("type",1);
                            resultList.add(map);
                        }
                    }else if("2".equals(type)){
                        if(department.getParentId()==userDept.getId()&&department.getDeptType()==4){
                            Map map = new HashMap();
                            map.put("id",department.getId());
                            map.put("name",department.getDeptName());
                            map.put("type",1);
                            resultList.add(map);
                        }
                    }
                }
                if("1".equals(type)){
                    for (UserInfo user : userInfoList){
                        if (user.getDeptId() == userDept.getId()&&user.getName().contains(searchName)) {
                            Map map = new HashMap();
                            map.put("id",user.getId());
                            map.put("name",user.getName());
                            map.put("type",2);
                            resultList.add(map);
                        }else{
                            for (Department subDept:subDepartmentList){
                                if(user.getDeptId() == subDept.getId()&&user.getName().contains(searchName)){
                                    Map map = new HashMap();
                                    map.put("id",user.getId());
                                    map.put("name",user.getName());
                                    map.put("type",2);
                                    resultList.add(map);
                                }
                            }
                        }
                    }
                }else if("2".equals(type)){
                    for (CarInfo car : carInfoList){
                        for (Department subDept:subDepartmentList){
                            if(car.getDeptId() == subDept.getId()&&car.getName().contains(searchName)){
                                Map map = new HashMap();
                                map.put("id",car.getId());
                                map.put("name",car.getName());
                                map.put("type",2);
                                resultList.add(map);
                            }
                        }
                    }
                }
            }else if(userInfo.getLevel()==1){
                if("1".equals(type)){
                    for (Department department:departmentList){
                        if(department.getDeptType()==3){
                            Map map = new HashMap();
                            map.put("id",department.getId());
                            map.put("name",department.getDeptName());
                            map.put("type",1);
                            resultList.add(map);
                        }else if(department.getDeptName().contains(searchName)&&department.getDeptType()!=4){
                            Map map = new HashMap();
                            map.put("id",department.getId());
                            map.put("name",department.getDeptName());
                            map.put("type",1);
                            resultList.add(map);
                        }
                    }
                    for (UserInfo user : userInfoList){
                        Map map = new HashMap();
                        map.put("id",user.getId());
                        map.put("name",user.getName());
                        map.put("type",2);
                        resultList.add(map);
                    }
                }else if("2".equals(type)){
                    for (Department department:departmentList){
                        if(department.getDeptType()==4){
                            Map map = new HashMap();
                            map.put("id",department.getId());
                            map.put("name",department.getDeptName());
                            map.put("type",1);
                            resultList.add(map);
                        }else if(department.getDeptName().contains(searchName)&&department.getDeptType()!=3){
                            Map map = new HashMap();
                            map.put("id",department.getId());
                            map.put("name",department.getDeptName());
                            map.put("type",2);
                            resultList.add(map);
                        }
                    }
                    for (CarInfo car : carInfoList){
                        Map map = new HashMap();
                        map.put("id",car.getId());
                        map.put("name",car.getName());
                        map.put("type",2);
                        resultList.add(map);
                    }
                }
            }

            resultData.put("resultList",resultList);
            resultJson.put("resultCode",0);
            resultJson.put("resultData",resultData);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }

        return resultJson;
    }
}