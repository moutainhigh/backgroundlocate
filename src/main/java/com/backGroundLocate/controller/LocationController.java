package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.*;
import com.backGroundLocate.service.*;
import com.backGroundLocate.util.LocationUtil;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.geom.Point2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags="定位管理接口")
@RequestMapping(value = "/location")
@RestController
public class LocationController {

    @Autowired
    private AuthService authService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private InstitutionalService institutionalService;

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LocationUtil locationUtil;
    /**
     * 人员定位上传接口
     * @param userId
     * @param lon
     * @param lat
     * @param lonLat
     * @param address
     * @param timestamp
     * @return
     */
    @PostMapping(value = "/saveUserLocation")
    public JSONObject saveUserLocation(@RequestParam(value = "userId") Integer userId,
                                       @RequestParam(value = "lon") String lon,
                                       @RequestParam(value = "lat") String lat,
                                       @RequestParam(value = "lonLat") String lonLat,
                                       @RequestParam(value = "address") String address,
                                       @RequestParam(value = "timestamp") Long timestamp
    ){
        System.out.println("======into saveUserLocation======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());
        Map paramMap = new HashMap<>();

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(Long.valueOf(timestamp*1000));
            BnsUserLocate bnsUserLocate = new BnsUserLocate();
            bnsUserLocate.setUserId(userId);
            bnsUserLocate.setAddress(address);
            bnsUserLocate.setLongitude(lon);
            bnsUserLocate.setLatitude(lat);
            bnsUserLocate.setLonLat(lonLat);
            bnsUserLocate.setUploadTime(simpleDateFormat.format(date));
            bnsUserLocate.setTimestamp(timestamp);
            locationService.createUserLocation(bnsUserLocate);

            paramMap.put("userId",userId);
            BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
            if(StringUtils.isEmpty(bnsUserNewestLocate)){
                locationService.createUserLocationForNewest(bnsUserLocate);
            }else{
                locationService.updateUserLocationForNewest(bnsUserLocate);
            }


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


    /**
     * 单位最新定位查询接口
     * @param userId
     * @param deptName
     * @param unitName
     * @param type
     * @return
     */
    @PostMapping(value = "/selectUnitLocationForNewest")
    public JSONObject selectUnitLocationForNewest(@RequestParam(value = "userId") Integer userId,
                                                  @RequestParam(value = "status",required = false) Integer status,
                                                  @RequestParam(value = "deptName",required = false) String deptName,
                                                  @RequestParam(value = "unitName",required = false) String unitName,
                                                  @RequestParam(value = "type") Integer type
    ){
        System.out.println("======into selectUnitLocationForNewest======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        Map userParamMap = new HashMap();
        Map vehicleParamMap = new HashMap();
        List<Map> resultList = new ArrayList<>();
        try {

            InsUser insUser = userService.selectUserById(userId);
            InsDepartment userDept = institutionalService.selectDepartmentById(insUser.getDeptId());

            AuthRole authRole = authService.selectRoleById(insUser.getRoleId());

            if(authRole.getRoleLevel()>=3){
                Map paramMap = new HashMap();
                if(!StringUtils.isEmpty(deptName)){
                    paramMap.put("deptName",deptName);
                    List<InsDepartment> insDepartmentList = institutionalService.selectDepartment(paramMap);
                    if(insDepartmentList.size()>0){
                        InsDepartment insDepartment = insDepartmentList.get(0);
                        if(insDepartment.getId() != userDept.getId()) {
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
                    userParamMap.put("userName",unitName);
                    vehicleParamMap.put("vehicleName",unitName);
                }
                userParamMap.put("deptId",insUser.getDeptId());
                vehicleParamMap.put("deptId",insUser.getDeptId());
                if(type == 1){
                    List<InsUser> userList = userService.selectUser(userParamMap);
                    for (InsUser user : userList){
                        Map map = new LinkedHashMap();
                        paramMap.put("userId",user.getId());
                        BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                        map.put("id",user.getId());
                        map.put("name",user.getUserName());
                        map.put("status",getUserStatus(user.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                        map.put("lng",bnsUserNewestLocate.getLongitude());
                        map.put("lat",bnsUserNewestLocate.getLatitude());
                        map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                        resultList.add(map);
                    }

                }else if(type == 2){
                    JSONArray vehicleArray = locationUtil.getExVehicles();
                    List<InsVehicle> vehicleList = vehicleService.selectVehicle(vehicleParamMap);
                    for (InsVehicle vehicle : vehicleList){
                        Map map = new LinkedHashMap();
                        map.put("id",vehicle.getId());
                        map.put("name",vehicle.getVehicleName());

                        for (int i=0;i<vehicleArray.size();i++){
                            JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                            if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                String lonLat = lon+","+lat;
                                Map<String,Object> vehicleMap = new HashMap();
                                vehicleMap.put("vehicleId",vehicle.getId());
                                vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                vehicleMap.put("vehicleLon",vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                vehicleMap.put("vehicleLat",vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                map.put("status",getVehicleStatus(vehicleMap));
                                map.put("lng",lon);
                                map.put("lat",lat);
                                map.put("latlon",lonLat);
                            }

                        }
                        resultList.add(map);
                    }
                }

            }else if(authRole.getRoleLevel()==2){
                Map paramMap = new HashMap();
                List<InsDepartment> subDepartmentList = institutionalService.selectSubDepartment(userDept.getId());

                if(!StringUtils.isEmpty(deptName)){
                    paramMap.put("deptName",deptName);
                    List<InsDepartment> insDepartmentList = institutionalService.selectDepartment(paramMap);

                    if(insDepartmentList.size()>0){
                        InsDepartment insDepartment = insDepartmentList.get(0);
                        if(insDepartment.getDeptLevel()==1){
                            resultJson.put("resultCode", 1);
                            resultJson.put("resultMessage", "权限不足,无法查询");
                            return resultJson;

                        }else if(insDepartment.getDeptLevel() ==2){
                            if(insDepartment.getId() == userDept.getId()){
                                if(type == 1){
                                    List<InsUser> dirUserList = userService.selectDirectlyUser(insDepartment.getId());
                                    for (InsUser dirUser : dirUserList){
                                        Map map = new LinkedHashMap();
                                        paramMap.put("userId",dirUser.getId());
                                        BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                        map.put("id",dirUser.getId());
                                        map.put("name",dirUser.getUserName());
                                        map.put("status",getUserStatus(dirUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                        map.put("lng",bnsUserNewestLocate.getLongitude());
                                        map.put("lat",bnsUserNewestLocate.getLatitude());
                                        map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                        resultList.add(map);

                                    }
                                    for (InsDepartment subDept : subDepartmentList){
                                        if(subDept.getDeptType() == 3){
                                            List<InsUser> subUserList = userService.selectDirectlyUser(subDept.getId());
                                            for (InsUser subUser : subUserList){
                                                Map map = new LinkedHashMap();
                                                paramMap.put("userId",subUser.getId());
                                                BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                                map.put("id",subUser.getId());
                                                map.put("name",subUser.getUserName());
                                                map.put("status",getUserStatus(subUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                                map.put("lng",bnsUserNewestLocate.getLongitude());
                                                map.put("lat",bnsUserNewestLocate.getLatitude());
                                                map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                                resultList.add(map);
                                            }
                                        }
                                    }
                                }else if(type == 2){
                                    for (InsDepartment subDept : subDepartmentList){
                                        if(subDept.getDeptType() == 4){
                                            vehicleParamMap.put("deptId",subDept.getId());
                                            JSONArray vehicleArray = locationUtil.getExVehicles();
                                            List<InsVehicle> vehicleList = vehicleService.selectVehicle(vehicleParamMap);
                                            for (InsVehicle vehicle : vehicleList){
                                                Map map = new LinkedHashMap();
                                                map.put("id",vehicle.getId());
                                                map.put("name",vehicle.getVehicleName());
                                                for (int i=0;i<vehicleArray.size();i++){
                                                    JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                                                    if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                                        JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                                        String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                                        String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                                        String lonLat = lon+","+lat;
                                                        Map vehicleMap = new HashMap();
                                                        vehicleMap.put("vehicleId",vehicle.getId());
                                                        vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                                        vehicleMap.put("vehicleLon",lon);
                                                        vehicleMap.put("vehicleLat",lat);
                                                        vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                                        vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                                        map.put("status",getVehicleStatus(vehicleMap));
                                                        map.put("lng",lon);
                                                        map.put("lat",lat);
                                                        map.put("latlon",lonLat);
                                                    }
                                                }
                                                resultList.add(map);
                                            }
                                        }
                                    }
                                }
                            }else{
                                resultJson.put("resultCode", 1);
                                resultJson.put("resultMessage", "权限不足,无法查询");
                                return resultJson;
                            }

                        }else if(insDepartment.getDeptLevel() ==3){
                            InsDepartmentRelation insDepartmentRelation = institutionalService.selectDeptRelationByDeptId(insDepartment.getId());
                            if(insDepartmentRelation.getParentDeptId() == userDept.getId()){
                                for (InsDepartment subDept : subDepartmentList){
                                    if(insDepartment.getId() == subDept.getId()){
                                        if(type == 1 && insDepartment.getDeptType() == 3){
                                            List<InsUser> subUserList = userService.selectDirectlyUser(insDepartment.getId());
                                            for (InsUser subUser : subUserList){
                                                Map map = new LinkedHashMap();
                                                paramMap.put("userId",subUser.getId());
                                                BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                                map.put("id",subUser.getId());
                                                map.put("name",subUser.getUserName());
                                                map.put("status",getUserStatus(subUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                                map.put("lng",bnsUserNewestLocate.getLongitude());
                                                map.put("lat",bnsUserNewestLocate.getLatitude());
                                                map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                                resultList.add(map);
                                            }
                                        }else if(type == 2 && insDepartment.getDeptType() == 4){
                                            vehicleParamMap.put("deptId",subDept.getId());
                                            JSONArray vehicleArray = locationUtil.getExVehicles();
                                            List<InsVehicle> vehicleList = vehicleService.selectVehicle(vehicleParamMap);
                                            for (InsVehicle vehicle : vehicleList){
                                                Map map = new LinkedHashMap();
                                                map.put("id",vehicle.getId());
                                                map.put("name",vehicle.getVehicleName());
                                                for (int i=0;i<vehicleArray.size();i++){
                                                    JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                                                    if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                                        JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                                        String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                                        String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                                        String lonLat = lon+","+lat;
                                                        Map vehicleMap = new HashMap();
                                                        vehicleMap.put("vehicleId",vehicle.getId());
                                                        vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                                        vehicleMap.put("vehicleLon",lon);
                                                        vehicleMap.put("vehicleLat",lat);
                                                        vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                                        vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                                        map.put("status",getVehicleStatus(vehicleMap));
                                                        map.put("lng",lon);
                                                        map.put("lat",lat);
                                                        map.put("latlon",lonLat);
                                                    }
                                                }
                                                resultList.add(map);
                                            }
                                        }
                                    }
                                }
                            }else{
                                resultJson.put("resultCode", 1);
                                resultJson.put("resultMessage", "权限不足,无法查询");
                                return resultJson;
                            }
                        }
                    }else{
                        resultJson.put("resultCode", 1);
                        resultJson.put("resultMessage", "未查询到部门");
                        return resultJson;
                    }
                }else if(!StringUtils.isEmpty(unitName)){
                    if(type == 1){
                        userParamMap.put("deptId",userDept.getId());
                        userParamMap.put("userName",unitName);
                        List<InsUser> dirUserList = userService.selectUser(userParamMap);
                        for (InsUser dirUser : dirUserList){
                            Map map = new LinkedHashMap();
                            paramMap.put("userId",dirUser.getId());
                            BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                            map.put("id",dirUser.getId());
                            map.put("name",dirUser.getUserName());
                            map.put("status",getUserStatus(dirUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                            map.put("lng",bnsUserNewestLocate.getLongitude());
                            map.put("lat",bnsUserNewestLocate.getLatitude());
                            map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                            resultList.add(map);

                        }
                        for (InsDepartment subDept : subDepartmentList){
                            userParamMap.put("deptId",subDept.getId());
                            List<InsUser> subUserList = userService.selectUser(userParamMap);
                            for (InsUser subUser : subUserList){
                                Map map = new LinkedHashMap();
                                paramMap.put("userId",subUser.getId());
                                BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                map.put("id",subUser.getId());
                                map.put("name",subUser.getUserName());
                                map.put("status",getUserStatus(subUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                map.put("lng",bnsUserNewestLocate.getLongitude());
                                map.put("lat",bnsUserNewestLocate.getLatitude());
                                map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                resultList.add(map);
                            }
                        }
                    }else if(type == 2){
                        for (InsDepartment subDept : subDepartmentList){
                            vehicleParamMap.put("deptId",subDept.getId());
                            vehicleParamMap.put("vehicleName",unitName);
                            JSONArray vehicleArray = locationUtil.getExVehicles();
                            List<InsVehicle> vehicleList = vehicleService.selectVehicle(vehicleParamMap);
                            for (InsVehicle vehicle : vehicleList){
                                Map map = new LinkedHashMap();
                                map.put("id",vehicle.getId());
                                map.put("name",vehicle.getVehicleName());
                                for (int i=0;i<vehicleArray.size();i++){
                                    JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                                    if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                        JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                        String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                        String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                        String lonLat = lon+","+lat;
                                        Map vehicleMap = new HashMap();
                                        vehicleMap.put("vehicleId",vehicle.getId());
                                        vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                        vehicleMap.put("vehicleLon",lon);
                                        vehicleMap.put("vehicleLat",lat);
                                        vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                        vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                        map.put("status",getVehicleStatus(vehicleMap));
                                        map.put("lng",lon);
                                        map.put("lat",lat);
                                        map.put("latlon",lonLat);
                                    }
                                }
                                resultList.add(map);
                            }
                        }
                    }
                }else{
                    if(type == 1){
                        userParamMap.put("deptId",userDept.getId());
                        List<InsUser> dirUserList = userService.selectUser(userParamMap);
                        for (InsUser dirUser : dirUserList){
                            Map map = new LinkedHashMap();
                            paramMap.put("userId",dirUser.getId());
                            BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                            map.put("id",dirUser.getId());
                            map.put("name",dirUser.getUserName());
                            map.put("status",getUserStatus(dirUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                            map.put("lng",bnsUserNewestLocate.getLongitude());
                            map.put("lat",bnsUserNewestLocate.getLatitude());
                            map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                            resultList.add(map);

                        }
                        for (InsDepartment subDept : subDepartmentList){
                            if(subDept.getDeptType() == 3){
                                userParamMap.put("deptId",subDept.getId());
                                List<InsUser> subUserList = userService.selectUser(userParamMap);
                                for (InsUser subUser : subUserList){
                                    Map map = new LinkedHashMap();
                                    paramMap.put("userId",subUser.getId());
                                    BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                    map.put("id",subUser.getId());
                                    map.put("name",subUser.getUserName());
                                    map.put("status",getUserStatus(subUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                    map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                    resultList.add(map);
                                }
                            }
                        }
                    }else if(type == 2){
                        for (InsDepartment subDept : subDepartmentList){
                            if(subDept.getDeptType() == 4){
                                vehicleParamMap.put("deptId",subDept.getId());
                                JSONArray vehicleArray = locationUtil.getExVehicles();
                                List<InsVehicle> vehicleList = vehicleService.selectVehicle(vehicleParamMap);
                                for (InsVehicle vehicle : vehicleList){
                                    Map map = new LinkedHashMap();
                                    map.put("id",vehicle.getId());
                                    map.put("name",vehicle.getVehicleName());
                                    for (int i=0;i<vehicleArray.size();i++){
                                        JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                                        if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                            JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                            String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                            String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                            String lonLat = lon+","+lat;
                                            Map vehicleMap = new HashMap();
                                            vehicleMap.put("vehicleId",vehicle.getId());
                                            vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                            vehicleMap.put("vehicleLon",lon);
                                            vehicleMap.put("vehicleLat",lat);
                                            vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                            vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                            map.put("status",getVehicleStatus(vehicleMap));
                                            map.put("lng",lon);
                                            map.put("lat",lat);
                                            map.put("latlon",lonLat);
                                        }
                                    }
                                    resultList.add(map);
                                }
                            }
                        }
                    }
                }

            }else if(authRole.getRoleLevel()==1){
                Map paramMap = new HashMap();
                List<InsDepartment> subDepartmentList = institutionalService.selectSubDepartment(userDept.getId());

                if(!StringUtils.isEmpty(deptName)){
                    paramMap.put("deptName",deptName);
                    List<InsDepartment> insDepartmentList = institutionalService.selectDepartment(paramMap);

                    if(insDepartmentList.size()>0){
                        InsDepartment insDepartment = insDepartmentList.get(0);
                        if(insDepartment.getDeptLevel()==1){
                            if(type == 1){
                                List<InsUser> userList = userService.selectUser(new HashMap());
                                for (InsUser user : userList){
                                    Map map = new LinkedHashMap();
                                    paramMap.put("userId",user.getId());
                                    BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                    map.put("id",user.getId());
                                    map.put("name",user.getUserName());
                                    map.put("status",map.put("status",getUserStatus(user.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude()))));
                                    map.put("lng",bnsUserNewestLocate.getLongitude());
                                    map.put("lat",bnsUserNewestLocate.getLatitude());
                                    map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                    resultList.add(map);
                                }
                            }else if(type == 2){
                                JSONArray vehicleArray = locationUtil.getExVehicles();
                                List<InsVehicle> vehicleList = vehicleService.selectVehicle(new HashMap());
                                for (InsVehicle vehicle : vehicleList){
                                    Map map = new LinkedHashMap();
                                    map.put("id",vehicle.getId());
                                    map.put("name",vehicle.getVehicleName());
                                    for (int i=0;i<vehicleArray.size();i++){
                                        JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                                        if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                            JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                            String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                            String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                            String lonLat = lon+","+lat;
                                            Map vehicleMap = new HashMap();
                                            vehicleMap.put("vehicleId",vehicle.getId());
                                            vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                            vehicleMap.put("vehicleLon",lon);
                                            vehicleMap.put("vehicleLat",lat);
                                            vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                            vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                            map.put("status",getVehicleStatus(vehicleMap));
                                            map.put("lng",lon);
                                            map.put("lat",lat);
                                            map.put("latlon",lonLat);
                                        }
                                    }
                                    resultList.add(map);
                                }
                            }

                        }else if(insDepartment.getDeptLevel() ==2){
                            if(type == 1){

                                for (InsDepartment subDept : subDepartmentList){
                                    if(insDepartment.getId() == subDept.getId()){
                                        List<InsUser> subUserList = userService.selectDirectlyUser(subDept.getId());
                                        for (InsUser subUser : subUserList){
                                            Map map = new LinkedHashMap();
                                            paramMap.put("userId",subUser.getId());
                                            BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                            map.put("id",subUser.getId());
                                            map.put("name",subUser.getUserName());
                                            map.put("status",getUserStatus(subUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                            map.put("lng",bnsUserNewestLocate.getLongitude());
                                            map.put("lat",bnsUserNewestLocate.getLatitude());
                                            map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                            resultList.add(map);
                                        }
                                        List<InsDepartment> dirDeptList = institutionalService.selectSubDepartment(subDept.getId());
                                        for (InsDepartment dirDept : dirDeptList){
                                            if(dirDept.getDeptType() == 3){
                                                userParamMap.put("deptId",dirDept.getId());
                                                List<InsUser> dirUserList = userService.selectUser(userParamMap);
                                                for (InsUser dirUser : dirUserList){
                                                    Map map = new LinkedHashMap();
                                                    paramMap.put("userId",dirUser.getId());
                                                    BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                                    map.put("id",dirUser.getId());
                                                    map.put("name",dirUser.getUserName());
                                                    map.put("status",getUserStatus(dirUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                                    map.put("lng",bnsUserNewestLocate.getLongitude());
                                                    map.put("lat",bnsUserNewestLocate.getLatitude());
                                                    map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                                    resultList.add(map);
                                                }
                                            }
                                        }
                                    }
                                }
                            }else if(type == 2){
                                for (InsDepartment subDept : subDepartmentList){
                                    if(insDepartment.getId() == subDept.getId()){
                                        List<InsDepartment> dirDeptList = institutionalService.selectSubDepartment(subDept.getId());
                                        for (InsDepartment dirDept : dirDeptList){
                                            if(dirDept.getDeptType() == 4){
                                                vehicleParamMap.put("deptId",dirDept.getId());
                                                JSONArray vehicleArray = locationUtil.getExVehicles();
                                                List<InsVehicle> vehicleList = vehicleService.selectVehicle(vehicleParamMap);
                                                for (InsVehicle vehicle : vehicleList){
                                                    Map map = new LinkedHashMap();
                                                    map.put("id",vehicle.getId());
                                                    map.put("name",vehicle.getVehicleName());
                                                    for (int i=0;i<vehicleArray.size();i++){
                                                        JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                                                        if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                                            JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                                            String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                                            String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                                            String lonLat = lon+","+lat;
                                                            Map vehicleMap = new HashMap();
                                                            vehicleMap.put("vehicleId",vehicle.getId());
                                                            vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                                            vehicleMap.put("vehicleLon",lon);
                                                            vehicleMap.put("vehicleLat",lat);
                                                            vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                                            vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                                            map.put("status",getVehicleStatus(vehicleMap));
                                                            map.put("lng",lon);
                                                            map.put("lat",lat);
                                                            map.put("latlon",lonLat);
                                                        }
                                                    }
                                                    resultList.add(map);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }else if(insDepartment.getDeptLevel() ==3){
                            if(type == 1 && insDepartment.getDeptType() == 3){
                                List<InsUser> subUserList = userService.selectDirectlyUser(insDepartment.getId());
                                for (InsUser subUser : subUserList){
                                    Map map = new LinkedHashMap();
                                    paramMap.put("userId",subUser.getId());
                                    BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                    map.put("id",subUser.getId());
                                    map.put("name",subUser.getUserName());
                                    map.put("status",getUserStatus(subUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                    map.put("lng",bnsUserNewestLocate.getLongitude());
                                    map.put("lat",bnsUserNewestLocate.getLatitude());
                                    map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                    resultList.add(map);
                                }
                            }else if(type == 2 && insDepartment.getDeptType() == 4){
                                vehicleParamMap.put("deptId",insDepartment.getId());
                                JSONArray vehicleArray = locationUtil.getExVehicles();
                                List<InsVehicle> vehicleList = vehicleService.selectVehicle(vehicleParamMap);
                                for (InsVehicle vehicle : vehicleList){
                                    Map map = new LinkedHashMap();
                                    map.put("id",vehicle.getId());
                                    map.put("name",vehicle.getVehicleName());
                                    for (int i=0;i<vehicleArray.size();i++){
                                        JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                                        if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                            JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                            String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                            String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                            String lonLat = lon+","+lat;
                                            Map vehicleMap = new HashMap();
                                            vehicleMap.put("vehicleId",vehicle.getId());
                                            vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                            vehicleMap.put("vehicleLon",lon);
                                            vehicleMap.put("vehicleLat",lat);
                                            vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                            vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                            map.put("status",getVehicleStatus(vehicleMap));
                                            map.put("lng",lon);
                                            map.put("lat",lat);
                                            map.put("latlon",lonLat);
                                        }
                                    }
                                    resultList.add(map);
                                }
                            }
                        }
                    }else{
                        resultJson.put("resultCode", 1);
                        resultJson.put("resultMessage", "未查询到部门");
                        return resultJson;
                    }
                }else if(!StringUtils.isEmpty(unitName)){
                    if(type == 1){
                        userParamMap.put("userName",unitName);
                        List<InsUser> userList = userService.selectUser(userParamMap);
                        for (InsUser user : userList){
                            Map map = new LinkedHashMap();
                            paramMap.put("userId",user.getId());
                            BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                            map.put("id",user.getId());
                            map.put("name",user.getUserName());
                            map.put("status",getUserStatus(user.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                            map.put("lng",bnsUserNewestLocate.getLongitude());
                            map.put("lat",bnsUserNewestLocate.getLatitude());
                            map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                            resultList.add(map);
                        }
                    }else if(type == 2){
                        vehicleParamMap.put("vehicleName",unitName);
                        JSONArray vehicleArray = locationUtil.getExVehicles();
                        List<InsVehicle> vehicleList = vehicleService.selectVehicle(vehicleParamMap);
                        for (InsVehicle vehicle : vehicleList){
                            Map map = new LinkedHashMap();
                            map.put("id",vehicle.getId());
                            map.put("name",vehicle.getVehicleName());
                            for (int i=0;i<vehicleArray.size();i++){
                                JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                                if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                    JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                    String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                    String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                    String lonLat = lon+","+lat;
                                    Map vehicleMap = new HashMap();
                                    vehicleMap.put("vehicleId",vehicle.getId());
                                    vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                    vehicleMap.put("vehicleLon",lon);
                                    vehicleMap.put("vehicleLat",lat);
                                    vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                    vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                    map.put("status",getVehicleStatus(vehicleMap));
                                    map.put("lng",lon);
                                    map.put("lat",lat);
                                    map.put("latlon",lonLat);
                                }
                            }
                            resultList.add(map);
                        }
                    }
                }else{
                    if(type == 1){
                        userParamMap.put("deptId",userDept.getId());
                        List<InsUser> userList = userService.selectUser(userParamMap);
                        for (InsUser user : userList){
                            Map map = new LinkedHashMap();
                            paramMap.put("userId",user.getId());
                            BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                            map.put("id",user.getId());
                            map.put("name",user.getUserName());
                            map.put("status",getUserStatus(user.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                            map.put("lng",bnsUserNewestLocate.getLongitude());
                            map.put("lat",bnsUserNewestLocate.getLatitude());
                            map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                            resultList.add(map);

                        }
                        for (InsDepartment subDept : subDepartmentList){
                            userParamMap.put("deptId",subDept.getId());
                            List<InsUser> subUserList = userService.selectUser(userParamMap);
                            for (InsUser subUser : subUserList){
                                Map map = new LinkedHashMap();
                                BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                map.put("id",subUser.getId());
                                map.put("name",subUser.getUserName());
                                map.put("status",getUserStatus(subUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                paramMap.put("userId",subUser.getId());
                                map.put("lng",bnsUserNewestLocate.getLongitude());
                                map.put("lat",bnsUserNewestLocate.getLatitude());
                                map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                resultList.add(map);
                            }
                            List<InsDepartment> dirDeptList = institutionalService.selectSubDepartment(subDept.getId());
                            for (InsDepartment dirDept : dirDeptList){
                                if(dirDept.getDeptType() == 3){
                                    userParamMap.put("deptId",dirDept.getId());
                                    List<InsUser> dirUserList = userService.selectUser(userParamMap);
                                    for (InsUser dirUser : dirUserList){
                                        Map map = new LinkedHashMap();
                                        BnsUserNewestLocate bnsUserNewestLocate = locationService.selectUserLocationForNewest(paramMap);
                                        map.put("id",dirUser.getId());
                                        map.put("name",dirUser.getUserName());
                                        map.put("status",getUserStatus(dirUser.getId(),Double.parseDouble(bnsUserNewestLocate.getLongitude()),Double.parseDouble(bnsUserNewestLocate.getLatitude())));
                                        paramMap.put("userId",dirUser.getId());
                                        map.put("lng",bnsUserNewestLocate.getLongitude());
                                        map.put("lat",bnsUserNewestLocate.getLatitude());
                                        map.put("latlon",bnsUserNewestLocate.getLonLat() == null ? "" : bnsUserNewestLocate.getLonLat());
                                        resultList.add(map);
                                    }
                                }
                            }
                        }
                    }else if(type == 2){
                        for (InsDepartment subDept : subDepartmentList){
                            List<InsDepartment> dirDeptList = institutionalService.selectSubDepartment(subDept.getId());
                            for (InsDepartment dirDept : dirDeptList){
                                if(dirDept.getDeptType() == 4){
                                    vehicleParamMap.put("deptId",dirDept.getId());
                                    JSONArray vehicleArray = locationUtil.getExVehicles();
                                    List<InsVehicle> vehicleList = vehicleService.selectVehicle(vehicleParamMap);
                                    for (InsVehicle vehicle : vehicleList){
                                        Map map = new LinkedHashMap();
                                        map.put("id",vehicle.getId());
                                        map.put("name",vehicle.getVehicleName());
                                        for (int i=0;i<vehicleArray.size();i++){
                                            JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                                            if(vehicle.getVehicleName().equals(vehicleObj.get("name"))){
                                                JSONObject vehicleJson = locationUtil.getExVehicleLocationForNewest(vehicleObj.getString("id"),vehicleObj.getString("vKey"));
                                                String lon = String.valueOf(vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
                                                String lat = String.valueOf(vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
                                                String lonLat = lon+","+lat;
                                                Map vehicleMap = new HashMap();
                                                vehicleMap.put("vehicleId",vehicle.getId());
                                                vehicleMap.put("vehicleName",vehicle.getVehicleName());
                                                vehicleMap.put("vehicleLon",lon);
                                                vehicleMap.put("vehicleLat",lat);
                                                vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
                                                vehicleMap.put("vehicleExState",vehicleJson.getString("state"));
                                                map.put("status",getVehicleStatus(vehicleMap));
                                                map.put("lng",lon);
                                                map.put("lat",lat);
                                                map.put("latlon",lonLat);
                                            }
                                        }
                                        resultList.add(map);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(!StringUtils.isEmpty(status)){
                List<Map> list = new ArrayList<>();
                for (Map map : resultList){
                   if(map.get("status") == status){
                       list.add(map);
                   }
                }
                resultData.put("unitLocationList",list);
            }else{
                resultData.put("unitLocationList",resultList);
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

    /**
     * 单位轨迹查询接口
     * @param type
     * @param startTimestamp
     * @param endTimestamp
     * @param unitId
     * @return
     */
    @PostMapping(value = "/selectUnitTrackInfo")
    public JSONObject selectUnitTrackInfo(@RequestParam(value = "unitId") Integer unitId,
                                          @RequestParam(value = "type") Integer type,
                                          @RequestParam(value = "startTimestamp",required = false) Long startTimestamp,
                                          @RequestParam(value = "endTimestamp",required = false) Long endTimestamp
    ){
        System.out.println("======into selectUnitTrackInfo======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap());
        JSONObject resultData = new JSONObject(new LinkedHashMap());
        JSONObject unitLocationInfo = new JSONObject(new LinkedHashMap());
        Map paramMap;
        try {
            if (type == 1) {

                InsUser insUser = userService.selectUserById(unitId);
                if(!StringUtils.isEmpty(insUser)){
                    if(StringUtils.isEmpty(startTimestamp)){
                        startTimestamp = initDateByDay().getTime()/1000;
                    }
                    if(StringUtils.isEmpty(endTimestamp)){
                        endTimestamp = System.currentTimeMillis()/1000;
                    }
                    paramMap = new HashMap();
                    paramMap.put("tableName","t_ins_user");
                    paramMap.put("startTimestamp",startTimestamp);
                    paramMap.put("endTimestamp",endTimestamp);
                    paramMap.put("unitId",unitId);
                    int illegalNum = locationService.selectIllegalNum(paramMap);
                    List<LinkedHashMap> locationList = locationService.selectUserTrackList(paramMap);
                    if(locationList.size()>0){
                        unitLocationInfo.put("id",insUser.getId());
                        unitLocationInfo.put("name",insUser.getUserName());
                        unitLocationInfo.put("violation",illegalNum);
                        unitLocationInfo.put("information",locationList);
                    }else{
                        resultJson.put("resultCode",1);
                        resultJson.put("resultMessage","未查询到人员轨迹");
                        return resultJson;
                    }
                }else{
                    resultJson.put("resultCode",1);
                    resultJson.put("resultMessage","未查询到人员信息");
                    return resultJson;
                }


            }else if(type == 2){
                if(StringUtils.isEmpty(startTimestamp)){
                    startTimestamp = initDateByDay().getTime();
                }else{
                    startTimestamp = startTimestamp*1000;
                }
                if(StringUtils.isEmpty(endTimestamp)){
                    endTimestamp = System.currentTimeMillis();
                }else{
                    endTimestamp = endTimestamp*1000;
                }
                InsVehicle insVehicle = vehicleService.selectVehicleById(unitId);

                if(!StringUtils.isEmpty(insVehicle)){
                    paramMap = new HashMap();
                    paramMap.put("tableName","t_ins_vehicle");
                    paramMap.put("startTimestamp",startTimestamp);
                    paramMap.put("endTimestamp",endTimestamp);
                    paramMap.put("unitId",unitId);
                    Integer illegalNum = locationService.selectIllegalNum(paramMap);
                    List<LinkedHashMap> locationList = new ArrayList<>();
                    JSONArray vehicleArray = locationUtil.getExVehicles();
                    for (int i=0;i<vehicleArray.size();i++){
                        JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                        if(insVehicle.getVehicleName().equals(vehicleObj.get("name"))){
                            JSONArray vehicleHisArray = locationUtil.getExVehicleLocationForHistory(vehicleObj.getString("id"),
                                    vehicleObj.getString("vKey"),
                                    startTimestamp.toString(),
                                    endTimestamp.toString());
                            for(int j=0;j<vehicleHisArray.size();j++){
                                JSONObject vehicleHis = vehicleHisArray.getJSONObject(j);
                                String lon = String.valueOf(vehicleHis.getDouble("lng")+vehicleHis.getDouble("lng_xz")+0.0065);
                                String lat = String.valueOf(vehicleHis.getDouble("lat")+vehicleHis.getDouble("lng_xz")+0.00588);
                                String lonLat = lon+","+lat;
                                LinkedHashMap map = new LinkedHashMap();
                                map.put("lng",lon);
                                map.put("lat",lat);
                                map.put("lonlat",lonLat);
                                map.put("mileage",vehicleHis.getDouble("dis"));
                                map.put("direction",vehicleHis.getInteger("dir"));
                                String recvt = vehicleHis.getString("recvt");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = sdf.parse(recvt);
                                map.put("timestamp",date.getTime()/1000);
                                locationList.add(map);

                            }
                        }
                    }

                    if(locationList.size()>0){
                        unitLocationInfo.put("id",insVehicle.getId());
                        unitLocationInfo.put("name",insVehicle.getVehicleName());
                        unitLocationInfo.put("violation",illegalNum);
                        unitLocationInfo.put("information",locationList);
                    }else{
                        resultJson.put("resultCode",1);
                        resultJson.put("resultMessage","未查询到车辆轨迹");
                        return resultJson;
                    }
                }else{
                    resultJson.put("resultCode",1);
                    resultJson.put("resultMessage","未查询到车辆信息");
                    return resultJson;
                }
            }
            resultData.put("unitLocationInfo",unitLocationInfo);
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



    private Integer getUserStatus(Integer userId,Double longitude,Double latitude){
        Integer result = 1;
        Map paramMap = new HashMap();

        paramMap.put("userId",userId);
        paramMap.put("type",1);
        Attendance todayAttendance = attendanceService.selectAttendanceForToday(paramMap);
        if(todayAttendance == null){
            result = 4;
        }else{
            Point2D.Double point = new Point2D.Double(longitude,latitude);
            List<BnsArea> bnsAreaList = locationService.selectArea(paramMap);
            List<Point2D.Double> polygon = new ArrayList<Point2D.Double>();
            if(bnsAreaList.size()>0){
                for (BnsArea bnsArea : bnsAreaList){
                    paramMap.put("areaId",bnsArea.getId());
                    List<BnsAreaPoint> bnsAreaPointList = locationService.selectAreaPoint(paramMap);
                    for (BnsAreaPoint bnsAreaPoint : bnsAreaPointList){
                        polygon.add(new Point2D.Double(Double.parseDouble(bnsAreaPoint.getLongitude()), Double.parseDouble(bnsAreaPoint.getLatitude())));
                    }
                }
            }

            if(!locationUtil.contains(polygon,point)){
                result = 2;
            }
        }

        return result;
    }

    private Integer getVehicleStatus(Map vehicleMap){
        Integer result = 1;
        Map paramMap = new HashMap();

        String vehicleId = vehicleMap.get("vehicleId").toString();
        String vehicleName = vehicleMap.get("vehicleName").toString();

        Double speed = Double.parseDouble(vehicleMap.get("vehicleSpeed").toString());
        String exState = vehicleMap.get("vehicleExState").toString();

        if(exState.contains("超时")){
            result = 4;
        }else{
            Point2D.Double point = new Point2D.Double(Double.parseDouble(vehicleMap.get("vehicleLon").toString()),Double.parseDouble(vehicleMap.get("vehicleLat").toString()));
            List<BnsArea> bnsAreaList = locationService.selectArea(paramMap);
            List<Point2D.Double> polygon = new ArrayList<Point2D.Double>();
            if(bnsAreaList.size()>0){
                for (BnsArea bnsArea : bnsAreaList){
                    paramMap.put("areaId",bnsArea.getId());
                    List<BnsAreaPoint> bnsAreaPointList = locationService.selectAreaPoint(paramMap);
                    for (BnsAreaPoint bnsAreaPoint : bnsAreaPointList){
                        polygon.add(new Point2D.Double(Double.parseDouble(bnsAreaPoint.getLongitude()), Double.parseDouble(bnsAreaPoint.getLatitude())));
                    }
                }
            }
            if(!locationUtil.contains(polygon,point)){
                result = 3;
            }else if(speed > 30){
                result = 2;
            }

        }

//        vehicleMap.put("vehicleId",vehicle.getId());
//        vehicleMap.put("vehicleName",vehicle.getVehicleName());
//        vehicleMap.put("vehicleLon",vehicleJson.getDouble("lng")+vehicleJson.getDouble("lng_xz")+0.0065);
//        vehicleMap.put("vehicleLat",vehicleJson.getDouble("lat")+vehicleJson.getDouble("lng_xz")+0.00588);
//        vehicleMap.put("vehicleSpeed",vehicleJson.getDouble("speed"));
//        vehicleMap.put("vehicleExState",vehicleJson.getString("state"));


        return result;
    }

    /**
     * 获得当天零时零分零秒
     * @return
     */
    private Date initDateByDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }


    private List<String> getTimeList(String startDate, String endxDate){
        SimpleDateFormat sdf ;
        int calendarType;

        switch (startDate.length()){
            case 10:
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendarType = Calendar.DATE;
                break;
            case 7:
                sdf = new SimpleDateFormat("yyyy-MM");
                calendarType = Calendar.MONTH;
                break;
            case 4:
                sdf = new SimpleDateFormat("yyyy");
                calendarType = Calendar.YEAR;
                break;
            default:
                return null;
        }

        List<String> result = new ArrayList<>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        try {
            min.setTime(sdf.parse(startDate));
            min.add(calendarType, 0);
            max.setTime(sdf.parse(endxDate));
            max.add(calendarType, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(min.getTime()));
            curr.add(calendarType, 1);
        }
        return result;
    }
}
