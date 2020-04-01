package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.odps.udf.CodecCheck;
import com.backGroundLocate.entity.Attendance;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.AttendanceService;
import com.backGroundLocate.service.UserInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(tags="考勤接口")
@RestController
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private Environment env;


    @RequestMapping(value = "/signInOrOut")
    public JSONObject signInOrOut(@RequestParam(value = "userId") String userId,
                                  @RequestParam(value = "lonlat") String lonlat,
                                  @RequestParam(value = "address") String address,
                                  @RequestParam(value = "type") String type){
        System.out.println("======into signInOrOut======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//考勤时间格式化
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");//打卡时间格式化
            Date date = new Date();
            Date inTime = format.parse("08:00:00");
            Date lateTime = format.parse("08:30:00");
            Date outTime = format.parse("17:00:00");
            Date nowTime = format.parse(format.format(date));


            UserInfo userInfo = userInfoService.selectUserById(Integer.parseInt(userId));

            Map paraMap = new HashMap();
            paraMap.put("userId",userId);
            Attendance todayAttendance = attendanceService.selectAttendanceForToday(paraMap);
            if("0".equals(type)){
                if(todayAttendance == null){
                    Attendance attendance = new Attendance();
                    attendance.setUserId(userInfo.getId());
                    attendance.setUserName(userInfo.getName());
                    attendance.setLonlat(lonlat);
                    attendance.setAddress(address);
                    attendance.setType(1);
                    if (nowTime.before(lateTime) && nowTime.after(inTime)) {//迟到
                        attendance.setState(2);
                    } else if (nowTime.after(lateTime)) {//旷工
                        attendance.setState(3);
                    } else if (nowTime.before(inTime)){
                        attendance.setAttendanceTime(date);
                        attendance.setState(1);
                        attendanceService.saveAttendance(attendance);
                    }
                }else{
                    resultJson.put("resultCode",1);
                    resultJson.put("resultMessage","已签到,请勿重复签到");
                }
            }else if("1".equals(type)){
                if(todayAttendance != null){
                    Attendance attendance = new Attendance();
                    attendance.setUserId(userInfo.getId());
                    attendance.setUserName(userInfo.getName());
                    attendance.setLonlat(lonlat);
                    attendance.setAddress(address);
                    attendance.setType(2);
                    if (nowTime.before(outTime)) {//早退
                        attendance.setState(2);
                    } else if (nowTime.after(outTime)){
                        attendance.setAttendanceTime(date);
                        attendance.setState(1);
                        attendanceService.saveAttendance(attendance);
                    }
                }else{
                    resultJson.put("resultCode",1);
                    resultJson.put("resultMessage","未签到,请先签到");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }

        return resultJson;
    }
}
