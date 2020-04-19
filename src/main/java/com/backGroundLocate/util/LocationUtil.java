package com.backGroundLocate.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.service.ExLiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class LocationUtil {

    @Autowired
    private ExLiveService exLiveService;

    private String exGpsApiUrl = "http://47.104.179.40:89/gpsonline/GPSAPI";

    private String exAccount = "ydhb";

    private String exPassword = "123456";

    private String driveName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private String dbUrl = "jdbc:sqlserver://47.104.179.40:1433;DatabaseName=gserver_";

    private String dbUserName = "sa";

    private String dbPassWord = "1qaz_2wsx";

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    /**
     * 获取超越平台所有车辆
     * @return
     */
    public JSONArray getExVehicles(){
        JSONArray resultArray = new JSONArray();
        MultiValueMap<String, Object> paramMap= new LinkedMultiValueMap<>();
        paramMap.add("version","1");
        paramMap.add("method","loginSystem");
        paramMap.add("name",exAccount);
        paramMap.add("pwd",exPassword);
        String userStr = restTemplateUtil.PostFormData(exGpsApiUrl,paramMap);
        System.out.println(userStr);

        JSONObject userJson = JSONObject.parseObject(userStr);

        MultiValueMap<String, Object> userMap= new LinkedMultiValueMap<>();
        userMap.add("version","1");
        userMap.add("method","loadVehicles");
        userMap.add("uid",userJson.getString("uid"));
        userMap.add("uKey",userJson.getString("uKey"));
        String groupsStr = restTemplateUtil.PostFormData(exGpsApiUrl,userMap);
        System.out.println(groupsStr);
        JSONObject groupsJson = JSONObject.parseObject(groupsStr);
        JSONArray groupsArray = groupsJson.getJSONArray("groups");
        for(int i=0;i<groupsArray.size();i++){
            JSONArray vehicles = groupsArray.getJSONObject(i).getJSONArray("vehicles");
            resultArray.addAll(vehicles);
        }

        return resultArray;
    }

    /**
     * 获取车辆最新定位
     * @param vid
     * @param vKey
     * @return
     */
    public JSONObject getExVehicleLocationForNewest(String vid,String vKey){
        MultiValueMap<String, Object> vehicleMap= new LinkedMultiValueMap<>();
        vehicleMap.add("version","1");
        vehicleMap.add("method","loadLocation");
        vehicleMap.add("vid",vid);
        vehicleMap.add("vKey",vKey);

        String locationStr = restTemplateUtil.PostFormData(exGpsApiUrl,vehicleMap);
        System.out.println(locationStr);
        JSONObject locationJson = JSONObject.parseObject(locationStr);
        return locationJson.getJSONArray("locs").getJSONObject(0);
    }

    /**
     * 获取车辆历史轨迹
     * @param vid
     * @param vKey
     * @param bTime
     * @param eTime
     * @return
     */
    public JSONArray getExVehicleLocationForHistory(String vid,String vKey,String bTime,String eTime){
        MultiValueMap<String, Object> vehicleMap= new LinkedMultiValueMap<>();
        vehicleMap.add("version","1");
        vehicleMap.add("method","loadHistory");
        vehicleMap.add("vid",vid);
        vehicleMap.add("vKey",vKey);
        vehicleMap.add("bTime",bTime);
        vehicleMap.add("eTime",eTime);

        String locationStr = restTemplateUtil.PostFormData(exGpsApiUrl,vehicleMap);
        System.out.println(locationStr);
        JSONObject locationJson = JSONObject.parseObject(locationStr);
        return locationJson.getJSONArray("history");
    }

    /**
     * 判断点是否在多边形内
     * 步骤：
     * 		①声明一个“画笔”
     * 		②将“画笔”移动到多边形的第一个顶点
     * 		③用“画笔”按顺序将多边形的顶点连接起来
     * 		④用“画笔”将多边形的第一个点连起来，最终形成一个封闭的多边形
     * 		⑤用contains()方法判断点是否在多边形区域内
     * @param polygon	多边形
     * @param point		检测点
     * @return			点在多边形内返回true，否则返回false
     */
    public boolean contains(List<Point2D.Double> polygon, Point2D.Double point){

        GeneralPath p = new GeneralPath();

        Point2D.Double first = polygon.get(0);
        p.moveTo(first.x, first.y);

        for(Point2D.Double d : polygon){
            p.lineTo(d.x, d.y);
        }

        p.lineTo(first.x, first.y);
        p.closePath();

        return p.contains(point);
    }

    /**
     * 原生JDBC查询超越库车辆最新定位
     * @param carInfoList
     * @return
     *//*
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

    *//**
     * 原生JDBC查询超越库车辆历史轨迹
     * @param startTimestamp
     * @param endTimestamp
     * @param simNumber
     * @return
     *//*
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
    }*/

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
