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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
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

    /**
     * 人员定位上传接口
     * @param backgroundLocateUser
     * @return
     */
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

    /**
     * 单位最新定位查询接口
     * @param userId
     * @param deptName
     * @param unitName
     * @param type
     * @return
     */
    @RequestMapping(value = "/selectUnitLocationForNewest")
    public JSONObject selectUnitLocationForNewest(@RequestParam(value = "userId") String userId,
                                                  @RequestParam(value = "status",required = false) String status,
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
                if(!StringUtils.isEmpty(status)){
                    userParamMap.put("status",status);
                    carParamMap.put("status",status);
                }
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
                if(!StringUtils.isEmpty(status)){
                    userParamMap.put("status",status);
                    carParamMap.put("status",status);
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
                if(!StringUtils.isEmpty(status)){
                    userParamMap.put("status",status);
                    carParamMap.put("status",status);
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

    /**
     * 单位轨迹查询接口
     * @param type
     * @param startTimestamp
     * @param endTimestamp
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/selectUnitTrackInfo")
    public JSONObject selectUnitTrackInfo(@RequestParam(value = "type") String type,
                                          @RequestParam(value = "startTimestamp",required = false) String startTimestamp,
                                          @RequestParam(value = "endTimestamp",required = false) String endTimestamp,
                                          @RequestParam(value = "unitId") String unitId){
        System.out.println("======into selectUnitTrackInfo======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject(new LinkedHashMap());
        JSONObject unitLocationInfo = new JSONObject(new LinkedHashMap());
        Map paramMap;
        try {
            if ("1".equals(type)) {

                UserInfo userInfo = userInfoService.selectUserById(Integer.parseInt(unitId));
                if(userInfo != null){
                    paramMap = new HashMap();
                    paramMap.put("startTimestamp",Integer.parseInt(startTimestamp));
                    paramMap.put("endTimestamp",Integer.parseInt(endTimestamp));
                    paramMap.put("unitId",Integer.parseInt(unitId));
                    int illegalNum = userInfoService.selectUserIllegalNum(paramMap);
                    List<LinkedHashMap> locationList = backgroundLocateUserService.selectUserTrackList(paramMap);
                    if(locationList.size()>0){
                        unitLocationInfo.put("id",userInfo.getId());
                        unitLocationInfo.put("name",userInfo.getName());
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


            }else if("2".equals(type)){
                long sTimestamp = 0L;
                long eTimestamp = 0L;
                if(StringUtils.isEmpty(startTimestamp)){
                    sTimestamp = initDateByDay().getTime();
                }else{
                    sTimestamp = Long.parseLong(startTimestamp)*1000;
                }

                if(StringUtils.isEmpty(endTimestamp)){
                    eTimestamp = new Date().getTime();
                }else{
                    eTimestamp = Long.parseLong(endTimestamp)*1000;
                }
                CarInfo conCar = new CarInfo();
                conCar.setId(Integer.parseInt(unitId));
                CarInfo carInfo = carInfoService.selectCar(conCar);

                if(carInfo !=null ){
                    paramMap = new HashMap();
                    paramMap.put("startTimestamp",Integer.parseInt(startTimestamp));
                    paramMap.put("endTimestamp",Integer.parseInt(endTimestamp));
                    paramMap.put("unitId",Integer.parseInt(unitId));
                    Integer illegalNum = carInfoService.selectCarIllegalNum(paramMap);
                    List locationList = getResultCarTrackList(sTimestamp,eTimestamp,carInfo.getSimNumber());
                    if(locationList.size()>0){
                        unitLocationInfo.put("id",carInfo.getId());
                        unitLocationInfo.put("name",carInfo.getName());
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

    /**
     * 原生JDBC查询超越库车辆最新定位
     * @param carInfoList
     * @return
     */
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
                System.out.println(exSql);
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
//                        resultMap.put("status",carInfo.getStatus());
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

    /**
     * 原生JDBC查询超越库车辆历史轨迹
     * @param startTimestamp
     * @param endTimestamp
     * @param simNumber
     * @return
     */
    private List getResultCarTrackList(long startTimestamp,long endTimestamp,String simNumber){
        List resultList = new LinkedList();

        Date sdate = new Date(startTimestamp);
        Date edate = new Date(endTimestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

        List<String> dayList = getTimeList(sdf1.format(sdate),sdf1.format(edate));
        List<String> monthList = getTimeList(sdf.format(sdate),sdf.format(edate));
        Integer vehidleId = exLiveService.selectselectVehicleIdBySimNumber(simNumber);
        for (String month :monthList){
            Connection conn = null;
            Statement stm = null;
            ResultSet rs = null;
            month = month.replace("-","");
            String sqlStart = "select * from (";

            String sqlContent = "";

            for (String day :dayList){
                day = day.replace("-","");
                if(day.contains(month)){
                    String tbName = "gps_"+day;
                    sqlContent  +=  "select cast((lng+lng3) as VARCHAR) +','+cast((lat+lat3) as VARCHAR) AS latlon," +
                                    "distance as mileage," +
                                    "DATEDIFF(second, '1970-01-01 08:00:00', recvtime) as timestamp" +
                                    " from "+ tbName +" where VehicleID = "+vehidleId+" union all ";
                }
            }

            String sqlCondition = ") a where 1=1 " +
                                  "and a.timestamp >= "+startTimestamp/1000+" " +
                                  "and a.timestamp <= "+endTimestamp/1000+" ";

            String sqlEnd = " order by a.timestamp asc";

            String exSql = sqlStart+sqlContent.substring(0,sqlContent.length()-10)+sqlCondition+sqlEnd;

            System.out.println(exSql);
            try {
                Class.forName(driveName);
                conn = DriverManager.getConnection(dbUrl+month, dbUserName, dbPassWord);
                stm = conn.createStatement();
                rs = stm.executeQuery(exSql);
                while(rs.next()){
                    Map map = new LinkedHashMap();
                    map.put("latlon",rs.getString("latlon"));
                    map.put("mileage",rs.getFloat("mileage"));
                    map.put("timestamp",rs.getInt("timestamp"));
                    resultList.add(map);
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
        return resultList;
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