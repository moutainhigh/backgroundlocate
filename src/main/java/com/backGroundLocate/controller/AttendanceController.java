package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.Attendance;
import com.backGroundLocate.entity.Leave;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.AttendanceService;
import com.backGroundLocate.service.LeaveService;
import com.backGroundLocate.service.UserInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags="考勤接口")
@RestController
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private Environment env;


    /**
     * 考勤状态查询
     * @param userId
     * @return
     */
    @RequestMapping(value = "/selectAttendanceStatus")
    public JSONObject selectAttendanceStatus(@RequestParam(value = "userId") String userId){
        System.out.println("======into selectAttendanceStatus======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();

        Map paraMap = new HashMap();
        paraMap.put("userId",userId);
        paraMap.put("type",1);
        Attendance todayAttendance = attendanceService.selectAttendanceForToday(paraMap);
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
    @RequestMapping(value = "/staffAttendance")
    public JSONObject staffAttendance(@RequestParam(value = "userId") String userId,
                                      @RequestParam(value = "lon") String lon,
                                      @RequestParam(value = "lat") String lat,
                                      @RequestParam(value = "address") String address,
                                      @RequestParam(value = "type") String type){
        System.out.println("======into staffAttendance======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();


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

            UserInfo userInfo = userInfoService.selectUserById(Integer.parseInt(userId));

            //规定区域判定未写

            Map paraMap = new HashMap();
            paraMap.put("userId",userId);
            paraMap.put("type",1);
            Attendance inAttendance = attendanceService.selectAttendanceForToday(paraMap);
            if("1".equals(type)){
                if(inAttendance == null){
                    Attendance attendance = new Attendance();
                    attendance.setUserId(userInfo.getId());
                    attendance.setUserName(userInfo.getName());
                    attendance.setLonlat(lon+","+lat);
                    attendance.setAddress(address);
                    attendance.setType(1);
                    attendance.setAttendanceTime(date);
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
            }else if("2".equals(type)){
                paraMap.put("type",2);

                if(inAttendance != null){
                    Attendance outAttendance = attendanceService.selectAttendanceForToday(paraMap);
                    if(outAttendance != null){
                        outAttendance.setLonlat(lon+","+lat);
                        outAttendance.setAddress(address);
                        outAttendance.setAttendanceTime(date);
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
                        attendance.setUserId(userInfo.getId());
                        attendance.setUserName(userInfo.getName());
                        attendance.setLonlat(lon+","+lat);
                        attendance.setAddress(address);
                        attendance.setType(2);
                        attendance.setAttendanceTime(date);
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
    @RequestMapping(value = "/requestForLeave")
    public JSONObject requestForLeave(@RequestParam(value = "userId") String userId,
                                      @RequestParam(value = "type") String type,
                                      @RequestParam(value = "remark") String remark,
                                      @RequestParam(value = "startTime") String startTime,
                                      @RequestParam(value = "endTime") String endTime,
                                      @RequestParam(value = "timestamp") String timestamp){
        System.out.println("======into requestForLeave======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();

        try {
            UserInfo userInfo = userInfoService.selectUserById(Integer.parseInt(userId));
            Leave leave = new Leave();
            leave.setUserId(userInfo.getId());
            leave.setUserName(userInfo.getName());
            leave.setType(Integer.parseInt(type));
            leave.setStartTime(Integer.parseInt(startTime));
            leave.setEndTime(Integer.parseInt(endTime));
            leave.setApproverState(0);
            leave.setTimestamp(Integer.parseInt(timestamp));
            leave.setRemark(remark);
            leaveService.saveLeave(leave);

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
    @RequestMapping(value = "/selectLeaveList")
    public JSONObject selectLeaveList(@RequestParam(value = "userId") String userId){
        System.out.println("======into selectLeaveList======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        JSONArray takeleaveList = new JSONArray();
        Map paramMap = new HashMap();

        try {
            paramMap.put("userId",Integer.parseInt(userId));
            List<Leave> leaveList = leaveService.selectLeaveList(paramMap);
            for (Leave leave : leaveList){
                Map map = new LinkedHashMap();
                map.put("id",leave.getId());
                map.put("timestamp",leave.getTimestamp());
                map.put("approverStatus",leave.getApproverState());
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
     * @param userId
     * @param leaveId
     * @return
     */
    @RequestMapping(value = "/selectLeaveDetail")
    public JSONObject selectLeaveDetail(@RequestParam(value = "userId") String userId,
                                        @RequestParam(value = "leaveId") String leaveId){
        System.out.println("======into selectLeaveDetail======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        JSONObject takeleaveInfo = new JSONObject(new LinkedHashMap<>());
        Map paramMap = new HashMap();

        try {
            paramMap.put("userId",Integer.parseInt(userId));
            paramMap.put("id",Integer.parseInt(leaveId));

            Leave leave = leaveService.selectLeave(paramMap);
            takeleaveInfo.put("id",leave.getId());
            takeleaveInfo.put("type",leave.getType());
            takeleaveInfo.put("startTime",leave.getStartTime());
            takeleaveInfo.put("endTime",leave.getEndTime());
            takeleaveInfo.put("remark",leave.getRemark());

            resultData.put("takeleaveInfo",takeleaveInfo);
            resultJson.put("resultData",resultData);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        return resultJson;
    }

}
