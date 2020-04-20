package com.backGroundLocate.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.BnsArea;
import com.backGroundLocate.entity.BnsAreaPoint;
import com.backGroundLocate.entity.InsVehicle;
import com.backGroundLocate.service.ExLiveService;
import com.backGroundLocate.service.LocationService;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
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

    @Autowired
    private LocationService locationService;

    @Autowired
    @Qualifier("mainJdbcTemplate")
    private JdbcTemplate mainJdbcTemplate;

    @Autowired
    @Qualifier("exLiveJdbcTemplate")
    private JdbcTemplate exLiveJdbcTemplate;

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
     * JDBC查询超越库车辆最新定位
     * @param vehicle
     * @return
     */
    public LinkedHashMap<String,Object> getVehicleLocationForEX(InsVehicle vehicle){
        LinkedHashMap<String,Object> resultMap = new LinkedHashMap<>();
        try {
            String lastRunDate = exLiveService.selectVehicleLastRunDate(vehicle.getSimNumber());
            Integer vehidleId = exLiveService.selectselectVehicleIdBySimNumber(vehicle.getSimNumber());
            String exSql = "select top 1 cast((lng+lng3) as VARCHAR) as lng," +
                    "cast((lat+lat3) as VARCHAR) as lat," +
                    "cast((lng+lng3) as VARCHAR) +','+cast((lat+lat3) as VARCHAR) AS latlon," +
                    "veo AS speed," +
                    "cstate AS state," +
                    "posinfo AS address," +
                    "totaldistance AS mileage," +
                    "recvtime " +
                    "from gps_"+lastRunDate+" where 1=1 " +
                    "and recvtime<getdate() " +
                    "and VehicleID = "+vehidleId+" order by recvtime DESC";
            System.out.println(exSql);

            JdbcTemplate hisJdbcTemplate = createHisJdbcTemplate("gserver_"+lastRunDate.substring(0,lastRunDate.length()-2));
            Map map = hisJdbcTemplate.queryForMap(exSql);
            String lng = "";
            String lat = "";
            String latlon = "";
            String speed = "";
            String state = "";
            String address = "";
            String mileage = "";
            String recvtime = "";

            if(map != null){
                lng = map.get("lng").toString();
                lat = map.get("lat").toString();
                latlon = map.get("latlon").toString();
                speed = map.get("speed").toString();
                state = map.get("state").toString();
                address = map.get("address").toString();
                mileage = map.get("mileage").toString();
                recvtime = map.get("recvtime").toString();
            }
            Map<String,Object> vehicleMap = new HashMap();
            vehicleMap.put("vehicleId",vehicle.getId());
            vehicleMap.put("vehicleName",vehicle.getVehicleName());
            vehicleMap.put("vehicleLon",lng);
            vehicleMap.put("vehicleLat",lat);
            vehicleMap.put("vehicleSpeed",speed);
            vehicleMap.put("vehicleExState",state);

            resultMap.put("id",vehicle.getId());
            resultMap.put("name",vehicle.getVehicleName());
            resultMap.put("type",vehicle.getTypeName());
            resultMap.put("lng",lng);
            resultMap.put("lat",lat);
            resultMap.put("latlon",latlon);
            resultMap.put("status",getVehicleStatus(vehicleMap));
            resultMap.put("address",address);
            resultMap.put("mileage",mileage);
            resultMap.put("recvtime",recvtime);

        }catch (Exception e){
            e.printStackTrace();
        }


        return resultMap;
    }



    /**
     * JDBC查询超越库车辆历史轨迹
     * @param startTimestamp
     * @param endTimestamp
     * @param simNumber
     * @return
     */
    public LinkedList<LinkedHashMap<String,Object>> getVehicleTrackForEX(Long startTimestamp, Long endTimestamp, String simNumber){
        LinkedList resultList = new LinkedList();
        Date sdate = new Date(startTimestamp);
        Date edate = new Date(endTimestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

        try {
            List<String> dayList = getTimeList(sdf1.format(sdate),sdf1.format(edate));
            List<String> monthList = getTimeList(sdf.format(sdate),sdf.format(edate));
            Integer vehidleId = exLiveService.selectselectVehicleIdBySimNumber(simNumber);
            for (String month :monthList){
                month = month.replace("-","");
                String sqlStart = "select * from (";
                String sqlContent = "";
                for (String day :dayList){
                    day = day.replace("-","");
                    if(day.contains(month)){
                        String tbName = "gps_"+day;
                        sqlContent  +=  "select cast((lng+lng3) as VARCHAR) as lng,cast((lat+lat3) as VARCHAR) as lat, cast((lng+lng3) as VARCHAR) +','+cast((lat+lat3) as VARCHAR) AS latlon," +
                                "distance as mileage," +
                                "recvtime," +
                                "direct," +
                                "DATEDIFF(second, '1970-01-01 08:00:00', recvtime) as timestamp" +
                                " from "+ tbName +" where VehicleID = "+vehidleId+" union all ";
                    }
                }
                String sqlCondition = ") a where 1=1 " +
                        "and a.timestamp >= "+startTimestamp/1000+" " +
                        "and a.timestamp <= "+endTimestamp/1000+" ";

                String sqlEnd = " order by a.timestamp asc";
                String exSql = sqlStart+sqlContent.substring(0,sqlContent.length()-10)+sqlCondition+sqlEnd;

                JdbcTemplate hisJdbcTemplate = createHisJdbcTemplate("gserver_"+month);
                System.out.println(exSql);

                List<Map<String,Object>> list = hisJdbcTemplate.queryForList(exSql);
                for(Map<String,Object> exMap : list){
                    LinkedHashMap map = new LinkedHashMap();
                    map.put("lng",exMap.get("lng"));
                    map.put("lat",exMap.get("lat"));
                    map.put("latlon",exMap.get("latlon"));
                    map.put("mileage",exMap.get("mileage"));
                    map.put("direction",exMap.get("direct"));
                    map.put("recvtime",exMap.get("recvtime").toString());
                    map.put("timestamp",exMap.get("timestamp"));
                    resultList.add(map);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultList;
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
            if(!contains(polygon,point)){
                result = 3;
            }else if(speed > 30){
                result = 2;
            }

        }
        return result;
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

    private JdbcTemplate createHisJdbcTemplate(String dbName){
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setIntegratedSecurity(false);
        dataSource.setDatabaseName(dbName);
        dataSource.setServerName("47.104.179.40");
        dataSource.setPortNumber(1433);
        dataSource.setUser("sa");
        dataSource.setPassword("1qaz_2wsx");

        return new JdbcTemplate(dataSource);
    }
}
