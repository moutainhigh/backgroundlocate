package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.*;
import com.backGroundLocate.service.*;
import com.backGroundLocate.util.LocationUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Api(tags = "组织机构管理接口")
@RestController
@RequestMapping(value = "/institutional")
public class InstitutionalManageController {

    @Autowired
    private InstitutionalService institutionalService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationUtil locationUtil;

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/selectUnitInfo")
    public JSONObject selectUnitInfo(@RequestParam(value = "userId") Integer userId,
                                     @RequestParam(value = "deptId",required = false) Integer deptId,
                                     @RequestParam(value = "unitId",required = false) Integer unitId,
                                     @RequestParam(value = "type") Integer type){
        System.out.println("======into selectUnitInfo======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());
        JSONObject information = new JSONObject(new LinkedHashMap<>());
        JSONArray deptList = new JSONArray();
        JSONArray unitList = new JSONArray();
        JSONObject departmentInfo = new JSONObject();
        try {
            if(!StringUtils.isEmpty(deptId)){
                List<InsDepartment> insDepartmentList = institutionalService.selectSubDepartment(deptId);
//                List<InsUser> directlyUserList = userService.selectDirectlyUser(deptId);
//                for(InsUser insUser : directlyUserList){
//                    Map map = new LinkedHashMap();
//                    map.put("id",insUser.getDeptId());
//                    map.put("name",insUser.getUserName());
//                    unitList.add(map);
//                }
                for (InsDepartment insDepartment : insDepartmentList){
                    Map deptMap = new LinkedHashMap();
                    if(type == 1 && insDepartment.getDeptType() != 4){
                        List<InsUser> insUserList = userService.selectDirectlyUser(insDepartment.getId());
                        for(InsUser insUser : insUserList){
                            Map map = new LinkedHashMap();
                            map.put("id",insUser.getDeptId());
                            map.put("name",insUser.getUserName());
                            unitList.add(map);
                        }
                        deptMap.put("id",insDepartment.getId());
                        deptMap.put("name",insDepartment.getDeptName());
                        deptList.add(deptMap);
                    }else if(type == 2 && insDepartment.getDeptType() != 3){
                        List<InsVehicle> vehicleList = vehicleService.selectDirectlyVehicle(insDepartment.getId());
                        for(InsVehicle insVehicle : vehicleList){
                            Map map = new LinkedHashMap();
                            map.put("id",insVehicle.getId());
                            map.put("name",insVehicle.getVehicleName());
                            unitList.add(map);
                        }
                        deptMap.put("id",insDepartment.getId());
                        deptMap.put("name",insDepartment.getDeptName());
                        deptList.add(deptMap);
                    }
                }

                departmentInfo.put("deptList",deptList);
                departmentInfo.put("unitList",unitList);
                departmentInfo.put("type",type);
                resultData.put("departmentInfo",departmentInfo);
            }else if(!StringUtils.isEmpty(unitId)){
                if (type == 1){
                    Map paramMap = new HashMap();
                    paramMap.put("userId",unitId);
                    InsUser insUser = userService.selectUserById(unitId);
                    if(!StringUtils.isEmpty(insUser)){
                        information.put("id",insUser.getId());
                        information.put("name",insUser.getUserName());
                        information.put("mobile",insUser.getPhoneNumber());
                        //待补班次
                        information.put("shift","A班");

                        BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                        information.put("address",bnsUserNewestLocate.getAddress());
                        information.put("status","正常");

                        paramMap.put("type",1);
                        Attendance inAttendance = attendanceService.selectAttendanceForToday(paramMap);
                        paramMap.put("type",2);
                        Attendance outAttendance = attendanceService.selectAttendanceForToday(paramMap);

                        paramMap.put("paramTime",System.currentTimeMillis()/1000);
                        List<AttLeave> attLeaveList = attendanceService.selectLeave(paramMap);
                        AttLeave attLeave = null;
                        if(attLeaveList.size()>0){
                            attLeave = attLeaveList.get(0);
                        }
                        if (!StringUtils.isEmpty(attLeave)){
                            switch (attLeave.getType()){
                                case 2 : information.put("attendance","事假");break;
                                case 3 : information.put("attendance","病假");break;
                            }
                        }else{
                            if (!StringUtils.isEmpty(inAttendance)) {
                                switch (inAttendance.getState()){
                                    case 1 : information.put("attendance","正常");break;
                                    case 2 : information.put("attendance","迟到");break;
                                    case 3 : information.put("attendance","旷工");break;
                                }
                                information.put("arriveTime",inAttendance.getTimestamp());
                            }else{
                                information.put("attendance","未签到");
                            }
                            if (!StringUtils.isEmpty(outAttendance)) {
                                switch (inAttendance.getState()){
                                    case 1 : information.put("attendance","正常");break;
                                    case 2 : information.put("attendance","早退");break;
                                    case 3 : information.put("attendance","旷工");break;
                                }
                                information.put("leaveTime",outAttendance.getTimestamp());
                            }
                            //待补班次
                        }
                    }else{
                        resultJson.put("resultCode",1);
                        resultJson.put("resultMessage","未查询到人员信息");
                    }
                }else if(type == 2){
                    InsVehicle insVehicle = vehicleService.selectVehicleById(unitId);
                    if(!StringUtils.isEmpty(insVehicle)){
                        information.put("id",insVehicle.getId());
                        information.put("name",insVehicle.getVehicleName());
                        information.put("type",insVehicle.getTypeName());
                        JSONArray vehicleArray = locationUtil.getExVehicles();
                        for (int i=0;i<vehicleArray.size();i++){
                            JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                            if(insVehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                information.put("address",vehicleJson.getString("info"));
                                information.put("status",vehicleJson.getString("state"));
                                information.put("mileage",vehicleJson.getString("totalDis"));

                            }
                        }
                        //待补车辆年审
                        information.put("annual","");
                        information.put("maintenance","");
                    }else{
                        resultJson.put("resultCode",1);
                        resultJson.put("resultMessage","未查询到车辆信息");
                    }


                }
                information.put("category",type);
                resultData.put("information",information);
            }else{

                InsUser insUser = userService.selectUserById(userId);

                InsDepartment insDepartment = institutionalService.selectDepartmentById(insUser.getDeptId());

                Map map = new LinkedHashMap();
                map.put("id",insDepartment.getId());
                map.put("name",insDepartment.getDeptName());
                deptList.add(map);

                if(type ==1){
                    List<InsUser> insUserList = userService.selectDirectlyUser(insUser.getDeptId());
                    for(InsUser user : insUserList){
                        Map userMap = new LinkedHashMap();
                        userMap.put("id",user.getDeptId());
                        userMap.put("name",user.getUserName());
                        unitList.add(userMap);
                    }
                }


                departmentInfo.put("deptList",deptList);
                departmentInfo.put("unitList",unitList);
                departmentInfo.put("type",type);

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


    @PostMapping(value = "/selectUnitInfoForFuzzy")
    public JSONObject selectUnitInfoForFuzzy(@RequestParam(value = "userId") Integer userId,
                                             @RequestParam(value = "type") Integer type,
                                             @RequestParam(value = "searchParam") String searchParam
    ){
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());
        JSONArray resultList = new JSONArray();
        Map paramMap = new HashMap();

        try {

            InsUser insUser = userService.selectUserById(userId);
            InsDepartment userDept = institutionalService.selectDepartmentById(insUser.getDeptId());

            AuthRole authRole = authService.selectRoleById(insUser.getRoleId());

            paramMap.put("deptName",searchParam);
            paramMap.put("userName",searchParam);
            paramMap.put("vehicleName",searchParam);

            List<InsDepartment> insDepartmentList = institutionalService.selectDepartment(paramMap);
            List<InsUser> insUserList = userService.selectUser(paramMap);
            List<InsVehicle> insVehicleList = vehicleService.selectVehicle(paramMap);

            if(authRole.getRoleLevel()>=3){

                for (InsDepartment insDepartment : insDepartmentList){
                    if(insDepartment.getId()==userDept.getId()){
                        Map map = new LinkedHashMap();
                        map.put("id",insDepartment.getId());
                        map.put("name",insDepartment.getDeptName());
                        map.put("type",1);
                        resultList.add(map);
                    }
                }

                if(type == 1){
                    for (InsUser user : insUserList){
                        if(user.getDeptId()==userDept.getId()){
                            Map map = new LinkedHashMap();
                            map.put("id",user.getId());
                            map.put("name",user.getUserName());
                            map.put("type",2);
                            resultList.add(map);
                        }
                    }
                }else if(type == 2){
                    for (InsVehicle insVehicle : insVehicleList){
                        if(insVehicle.getDeptId()==userDept.getId()){
                            Map map = new LinkedHashMap();
                            map.put("id",insVehicle.getId());
                            map.put("name",insVehicle.getVehicleName());
                            map.put("type",2);
                            resultList.add(map);
                        }
                    }
                }

            }else if(authRole.getRoleLevel()==2){
                List<InsDepartment> subDepartmentList = institutionalService.selectSubDepartment(userDept.getId());

                for (InsDepartment insDepartment : insDepartmentList){
                    if(insDepartment.getId()==userDept.getId()){
                        Map map = new LinkedHashMap();
                        map.put("id",insDepartment.getId());
                        map.put("name",insDepartment.getDeptName());
                        map.put("type",1);
                        resultList.add(map);
                    }
                }

                for (InsUser user : insUserList){
                    if(user.getUserName().equals(insUser.getUserName())){
                        Map map = new LinkedHashMap();
                        map.put("id",user.getId());
                        map.put("name",user.getUserName());
                        map.put("type",2);
                        resultList.add(map);
                    }
                }

                for (InsDepartment subDept : subDepartmentList){
                    if(type == 1){
                        if(subDept.getDeptType()==3 && subDept.getDeptName().contains(searchParam)){
                            Map map = new LinkedHashMap();
                            map.put("id",subDept.getId());
                            map.put("name",subDept.getDeptName());
                            map.put("type",1);
                            resultList.add(map);
                        }
                        for (InsUser user : insUserList){
                            if(user.getDeptId() == subDept.getId()){
                                Map map = new LinkedHashMap();
                                map.put("id",user.getId());
                                map.put("name",user.getUserName());
                                map.put("type",2);
                                resultList.add(map);
                            }
                        }
                    }else if(type == 2){
                        if(subDept.getDeptType()==4 && subDept.getDeptName().contains(searchParam)){
                            Map map = new LinkedHashMap();
                            map.put("id",subDept.getId());
                            map.put("name",subDept.getDeptName());
                            map.put("type",1);
                            resultList.add(map);
                        }
                        for (InsVehicle vehicle : insVehicleList){
                            if(vehicle.getDeptId() == subDept.getId()){
                                Map map = new LinkedHashMap();
                                map.put("id",vehicle.getId());
                                map.put("name",vehicle.getVehicleName());
                                map.put("type",2);
                                resultList.add(map);
                            }
                        }
                    }
                }

            }else if(authRole.getRoleLevel()==1){
                if(type == 1){
                    for (InsDepartment insDepartment : insDepartmentList){
                        if(insDepartment.getDeptType()<=3){
                            Map map = new LinkedHashMap();
                            map.put("id",insDepartment.getId());
                            map.put("name",insDepartment.getDeptName());
                            map.put("type",1);
                            resultList.add(map);
                        }
                    }
                    for (InsUser user : insUserList){
                        Map map = new LinkedHashMap();
                        map.put("id",user.getId());
                        map.put("name",user.getUserName());
                        map.put("type",2);
                        resultList.add(map);
                    }
                }else if(type == 2){
                    for (InsDepartment insDepartment:insDepartmentList){
                        if(insDepartment.getDeptType()==4){
                            Map map = new LinkedHashMap();
                            map.put("id",insDepartment.getId());
                            map.put("name",insDepartment.getDeptName());
                            map.put("type",1);
                            resultList.add(map);
                        }
                    }
                    for (InsVehicle vehicle : insVehicleList){
                        Map map = new LinkedHashMap();
                        map.put("id",vehicle.getId());
                        map.put("name",vehicle.getVehicleName());
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
