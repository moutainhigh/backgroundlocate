package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.BackgroundLocateUser;
import com.backGroundLocate.entity.CarInfo;
import com.backGroundLocate.entity.Department;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.*;
import com.backGroundLocate.util.JdbcUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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

    @Autowired
    private ExLiveService exLiveService;

    private String driveName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private String dbUrl = "jdbc:sqlserver://47.104.179.40:1433;DatabaseName=gserver_";

    private String dbUserName = "sa";

    private String dbPassWord = "1qaz_2wsx";

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
                                                  @RequestParam(value = "type") String type){
        System.out.println("======into selectUnitLocationForNewest======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        Map userParamMap = new HashMap();
        Map carParamMap = new HashMap();
        Department conDept;
        CarInfo conCar;
        List<Map> resultList = null;
        try {

            UserInfo userInfo = userInfoService.selectUserById(Integer.valueOf(userId));
            Department userDept = departmentService.selectDepartmentByPrimary(userInfo.getDeptId());
            userParamMap.put("userLevel",userInfo.getLevel());
            carParamMap.put("userLevel",userInfo.getLevel());
            System.out.println();
            if(userInfo.getLevel()>=3){
                conCar = new CarInfo();
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
                    userParamMap.put("unitName",unitName);
                    carParamMap.put("unitName",unitName);
                }
                userParamMap.put("deptId",userDept.getId());
                carParamMap.put("deptId",userDept.getId());
                if("1".equals(type)){
                    resultList = backgroundLocateUserNewestService.selectUserLocationForNewest(userParamMap);
                }else if("2".equals(type)){
                    List<CarInfo> carInfoList = carInfoService.selectCarForNewest(carParamMap);
                    resultList = getResultCarLocationList(carInfoList);
                }

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
                                userParamMap.put("onlyDeptName",deptName);
                                carParamMap.put("onlyDeptName",deptName);
                            }else if(itemDept.getId() == userDept.getId()){
                                //目标部门id与账号所属部门id相同，查询目标部门及下属所有部门人员信息
                                userParamMap.put("deptName",deptName);
                                userParamMap.put("deptId",userDept.getId());
                                userParamMap.put("parentId",userDept.getId());
                                carParamMap.put("deptName",deptName);
                                carParamMap.put("deptId",userDept.getId());
                                carParamMap.put("parentId",userDept.getId());
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
                    userParamMap.put("unitName",unitName);
                    userParamMap.put("deptId",userDept.getId());
                    userParamMap.put("parentId",userDept.getId());
                    carParamMap.put("unitName",unitName);
                    carParamMap.put("deptId",userDept.getId());
                    carParamMap.put("parentId",userDept.getId());
                }else{
                    userParamMap.put("deptName",userDept.getDeptName());
                    userParamMap.put("deptId",userDept.getId());
                    userParamMap.put("parentId",userDept.getId());
                    carParamMap.put("deptName",userDept.getDeptName());
                    carParamMap.put("deptId",userDept.getId());
                    carParamMap.put("parentId",userDept.getId());
                }
                if("1".equals(type)){
                    resultList = backgroundLocateUserNewestService.selectUserLocationForNewest(userParamMap);
                }else if("2".equals(type)){
                    List<CarInfo> carInfoList = carInfoService.selectCarForNewest(carParamMap);
                    resultList = getResultCarLocationList(carInfoList);
                }
            }else if(userInfo.getLevel()==1){
                if(!StringUtils.isEmpty(deptName)){
                    conDept = new Department();
                    conDept.setDeptName(deptName);
                    Department itemDept = departmentService.selectDepartment(conDept);
                    if(itemDept != null){
                        if(itemDept.getDeptLevel()==2){
                            //如果目标部门为2级部门,查询目标部门及下属所有部门人员信息
                            userParamMap.put("deptName",itemDept.getDeptName());
                            userParamMap.put("parentId",itemDept.getId());
                            carParamMap.put("deptName",itemDept.getDeptName());
                            carParamMap.put("parentId",itemDept.getId());
                        }else if(itemDept.getDeptLevel()==3){
                            //如果目标部门为3级部门,查询目标部门下属所有人员信息
                            userParamMap.put("onlyDeptName",itemDept.getDeptName());
                            carParamMap.put("onlyDeptName",itemDept.getDeptName());
                        }
                    }else {
                        resultJson.put("resultCode", 1);
                        resultJson.put("resultMessage", "未查询到部门");
                        return resultJson;
                    }

                }else if(!StringUtils.isEmpty(unitName)){
                    userParamMap.put("unitName",unitName);
                    carParamMap.put("unitName",unitName);
                }
                if("1".equals(type)){
                    resultList = backgroundLocateUserNewestService.selectUserLocationForNewest(userParamMap);
                }else if("2".equals(type)){
                    List<CarInfo> carInfoList = carInfoService.selectCarForNewest(carParamMap);
                    resultList = getResultCarLocationList(carInfoList);
                }
            }
            resultData.put("unitLocationList",resultList);

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

    private List getResultCarLocationList(List<CarInfo> carInfoList){
        List resultList = new ArrayList();
        Connection conn = null;
        Statement stm = null;
        ResultSet rs = null;
        if(carInfoList.size()>0){
            for (CarInfo carInfo : carInfoList){
                Integer vehidleId = exLiveService.selectselectVehicleIdBySimNumber(carInfo.getSimNumber());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String tableDate = sdf.format(new Date());
                String exSql = "select top 1 cast((lng+lng3) as VARCHAR) +','+cast((lat+lat3) as VARCHAR) AS latlon" +
                        " from gps_"+tableDate+" where recvtime<getdate() and VehicleID = "+vehidleId+" order by recvtime DESC";
                try {
                    Class.forName(driveName);
                    conn = DriverManager.getConnection(dbUrl+tableDate.substring(0,tableDate.length()-2), dbUserName, dbPassWord);
                    stm = conn.createStatement();
                    rs = stm.executeQuery(exSql);
                    String latlon = "";
                    while (rs.next()){
                        latlon = rs.getString("latlon");
                    }
                    if(!StringUtils.isEmpty(latlon)){
                        Map<String,Object> resultMap = new HashMap();
                        resultMap.put("id",carInfo.getId());
                        resultMap.put("name",carInfo.getName());
                        resultMap.put("latlon",latlon);
                        resultList.add(resultMap);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (stm != null) {
                            stm.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return resultList;
    }
}