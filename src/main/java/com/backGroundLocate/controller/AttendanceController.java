package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.*;
import com.backGroundLocate.service.AttendanceService;
import com.backGroundLocate.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags="考勤接口")
@RestController
@RequestMapping(value = "/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    /**
     * 考勤状态查询
     * @param userId
     * @return
     */
    @PostMapping(value = "/selectAttendanceStatus")
    public JSONObject selectAttendanceStatus(@RequestParam(value = "userId") String userId){
        System.out.println("======into selectAttendanceStatus======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());

        Map paramMap = new HashMap();
        paramMap.put("userId",userId);
        paramMap.put("type",1);
        Attendance todayAttendance = attendanceService.selectAttendanceForToday(paramMap);
        if(todayAttendance == null){
            resultData.put("status",1);
        }else{
            resultData.put("status",2);
        }
        resultJson.put("resultCode",0);
        resultJson.put("resultData",resultData);
        return resultJson;
    }

    /**
     * 人员考勤接口
     * @param userId
     * @param lon
     * @param lat
     * @param address
     * @param type
     * @return
     */
    @PostMapping(value = "/staffAttendance")
    public JSONObject staffAttendance(@RequestParam(value = "userId") Integer userId,
                                      @RequestParam(value = "lon") String lon,
                                      @RequestParam(value = "lat") String lat,
                                      @RequestParam(value = "address") String address,
                                      @RequestParam(value = "type") Integer type
    ){
        System.out.println("======into staffAttendance======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());

        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            Date nowTime = format.parse(format.format(date));

            Date inTime = format.parse("08:00:00");//上班时间
            Date lateTime = new Date(inTime.getTime()+(15*60*1000));//迟到时限
            Date absenteeismInTime = new Date(inTime.getTime()+(30*60*1000));//签到旷工时限
            System.out.println(inTime+","+lateTime+","+absenteeismInTime);

            Date outTime = format.parse("17:00:00");
            Date earlyTime = new Date(outTime.getTime()-(15*60*1000));//早退时限
            Date absenteeismOutTime = new Date(outTime.getTime()-(30*60*1000));//签退旷工时限
            System.out.println(outTime+","+earlyTime+","+absenteeismOutTime);

            InsUser insUser = userService.selectUserById(userId);

            //规定区域判定未写

            Map paraMap = new HashMap();
            paraMap.put("userId",userId);
            paraMap.put("type",1);
            Attendance inAttendance = attendanceService.selectAttendanceForToday(paraMap);
            if(type == 1){
                if(inAttendance == null){
                    Attendance attendance = new Attendance();
                    attendance.setUserId(insUser.getId());
                    attendance.setUserName(insUser.getUserName());
                    attendance.setLonlat(lon+","+lat);
                    attendance.setAddress(address);
                    attendance.setType(1);
                    attendance.setAttendanceTime(date);
                    attendance.setTimestamp(System.currentTimeMillis()/1000);
                    if (nowTime.before(absenteeismInTime) && nowTime.after(lateTime)) {
                        //旷工时限之前,迟到时限之后为迟到
                        attendance.setState(2);
                    } else if (nowTime.after(absenteeismInTime)) {
                        //旷工时限之后为旷工
                        attendance.setState(3);
                    } else if (nowTime.before(lateTime)){
                        //正常
                        attendance.setState(1);
                    }
                    attendanceService.saveAttendance(attendance);
                }else{
                    resultJson.put("resultCode",1);
                    resultJson.put("resultMessage","已签到,请勿重复签到");
                    return resultJson;
                }
                resultData.put("resultStatus","签到成功");
            }else if(type == 2){
                paraMap.put("type",2);

                if(inAttendance != null){
                    Attendance outAttendance = attendanceService.selectAttendanceForToday(paraMap);
                    if(outAttendance != null){
                        outAttendance.setLonlat(lon+","+lat);
                        outAttendance.setAddress(address);
                        outAttendance.setAttendanceTime(date);
                        outAttendance.setTimestamp(System.currentTimeMillis()/1000);
                        if (nowTime.after(absenteeismOutTime)&&nowTime.before(earlyTime)) {
                            //旷工时间之后,早退时间之前为早退
                            outAttendance.setState(2);
                        } else if (nowTime.before(absenteeismOutTime)){
                            //旷工时间之前为旷工
                            outAttendance.setState(3);
                        } else if (nowTime.after(earlyTime)){
                            //正常
                            outAttendance.setState(1);
                        }
                        attendanceService.updateAttendance(outAttendance);
                    }else{
                        Attendance attendance = new Attendance();
                        attendance.setUserId(insUser.getId());
                        attendance.setUserName(insUser.getUserName());
                        attendance.setLonlat(lon+","+lat);
                        attendance.setAddress(address);
                        attendance.setType(2);
                        attendance.setAttendanceTime(date);
                        attendance.setTimestamp(System.currentTimeMillis()/1000);
                        if (nowTime.after(absenteeismOutTime)&&nowTime.before(earlyTime)) {
                            //旷工时间之后,早退时间之前为早退
                            attendance.setState(2);
                        } else if (nowTime.before(absenteeismOutTime)){
                            //旷工时间之前为旷工
                            attendance.setState(3);
                        } else if (nowTime.after(earlyTime)){
                            //正常
                            attendance.setState(1);
                        }
                        attendanceService.saveAttendance(attendance);
                    }
                    resultData.put("resultStatus","签退成功");
                }else{
                    resultJson.put("resultCode",1);
                    resultJson.put("resultMessage","未签到,请先签到");
                    return resultJson;
                }
            }
            resultJson.put("resultCode",0);
            resultJson.put("resultData",resultData);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        return resultJson;
    }


    /**
     * 请假申请
     * @param userId
     * @param type
     * @param remark
     * @param startTime
     * @param endTime
     * @param timestamp
     * @return
     */
    @PostMapping(value = "/requestForLeave")
    public JSONObject requestForLeave(@RequestParam(value = "userId") Integer userId,
                                      @RequestParam(value = "type") Integer type,
                                      @RequestParam(value = "remark") String remark,
                                      @RequestParam(value = "startTime") Long startTime,
                                      @RequestParam(value = "endTime") Long endTime,
                                      @RequestParam(value = "timestamp") Long timestamp
    ){
        System.out.println("======into requestForLeave======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());

        try {
            InsUser insUser = userService.selectUserById(userId);
            AttLeave attLeave = new AttLeave();
            attLeave.setUserId(insUser.getId());
            attLeave.setUserName(insUser.getUserName());
            attLeave.setType(type);
            attLeave.setStartTime(startTime);
            attLeave.setEndTime(endTime);
            attLeave.setTimestamp(timestamp);
            attLeave.setRemark(remark);
            attendanceService.createLeave(attLeave);

            resultJson.put("resultCode",0);
            resultData.put("resultStatus","success");
            resultJson.put("resultData",resultData);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        return resultJson;
    }

    /**
     * 请假列表查询
     * @param userId
     * @return
     */
    @PostMapping(value = "/selectLeaveList")
    public JSONObject selectLeaveList(@RequestParam(value = "userId") Integer userId){
        System.out.println("======into selectLeaveList======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());
        JSONArray takeleaveList = new JSONArray();
        Map paramMap = new HashMap();

        try {
            paramMap.put("userId",userId);
            List<AttLeave> leaveList = attendanceService.selectLeave(paramMap);
            for (AttLeave attLeave : leaveList){
                Map map = new LinkedHashMap();
                map.put("id",attLeave.getId());
                map.put("timestamp",attLeave.getTimestamp());
                map.put("approvalStatus",attLeave.getApprovalState());
                takeleaveList.add(map);
            }
            resultData.put("takeleaveList",takeleaveList);
            resultJson.put("resultData",resultData);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        return resultJson;
    }

    /**
     * 请假详情查询
     * @param leaveId
     * @return
     */
    @PostMapping(value = "/selectLeaveDetail")
    public JSONObject selectLeaveDetail(@RequestParam(value = "leaveId") Integer leaveId){
        System.out.println("======into selectLeaveDetail======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());
        JSONObject takeleaveInfo = new JSONObject(new LinkedHashMap<>());

        try {
            AttLeave attleave = attendanceService.selectLeaveById(leaveId);
            if(!StringUtils.isEmpty(attleave)){
                takeleaveInfo.put("type",attleave.getType());
                takeleaveInfo.put("startTime",attleave.getStartTime());
                takeleaveInfo.put("endTime",attleave.getEndTime());
                takeleaveInfo.put("remark",attleave.getRemark());

                resultData.put("takeleaveInfo",takeleaveInfo);
                resultJson.put("resultData",resultData);
            }else{
                resultJson.put("resultCode",1);
                resultJson.put("resultMessage","未查询到请假申请信息");
                return resultJson;

            }
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        return resultJson;
    }

}
