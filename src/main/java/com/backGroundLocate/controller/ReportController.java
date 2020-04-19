package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.AttLeave;
import com.backGroundLocate.entity.Attendance;
import com.backGroundLocate.entity.InsDepartment;
import com.backGroundLocate.entity.InsUser;
import com.backGroundLocate.service.*;
import com.backGroundLocate.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags="报表接口")
@RequestMapping(value = "/report")
@RestController
public class ReportController {

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
    private DateUtil dateUtil;


    @PostMapping(value = "/userAttDayReport")
    public JSONObject userAttDayReport(@RequestParam(value = "userId") Integer userId){
        System.out.println("======into userAttDayReport======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());
        JSONObject resultEntity = new JSONObject(new LinkedHashMap<>());

        int allPeop = 0;
        int signPeop = 0;
        int dailyPeop = 0;
        int latePeop = 0;
        int earlyPeop = 0;

        try {
            InsUser insUser = userService.selectUserById(userId);
            InsDepartment userDept = institutionalService.selectDepartmentById(insUser.getDeptId());
            List<InsUser> userList = userService.selectDirectlyUser(userDept.getId());
            Map<String, Integer> numMap = getAttDayNum(userList);
            allPeop += numMap.get("allPeop");
            signPeop += numMap.get("signPeop");
            dailyPeop += numMap.get("dailyPeop");
            latePeop += numMap.get("latePeop");
            earlyPeop += numMap.get("earlyPeop");


            List<InsDepartment> dirDeptList = institutionalService.selectSubDepartment(userDept.getId());
            if(dirDeptList.size()>0){
                for (InsDepartment dirDept : dirDeptList){
                    List<InsUser> dirUserList = userService.selectDirectlyUser(dirDept.getId());
                    Map<String, Integer> dirNumMap = getAttDayNum(dirUserList);
                    allPeop += dirNumMap.get("allPeop");
                    signPeop += dirNumMap.get("signPeop");
                    dailyPeop += dirNumMap.get("dailyPeop");
                    latePeop += dirNumMap.get("latePeop");
                    earlyPeop += dirNumMap.get("earlyPeop");


                    List<InsDepartment> subDeptList = institutionalService.selectSubDepartment(dirDept.getId());
                    if(subDeptList.size()>0){
                        for (InsDepartment subDept : subDeptList){
                            List<InsUser> subUserList = userService.selectDirectlyUser(subDept.getId());
                            Map<String, Integer> subNumMap = getAttDayNum(subUserList);
                            allPeop += subNumMap.get("allPeop");
                            signPeop += subNumMap.get("signPeop");
                            dailyPeop += subNumMap.get("dailyPeop");
                            latePeop += subNumMap.get("latePeop");
                            earlyPeop += subNumMap.get("earlyPeop");
                        }
                    }
                }
            }


            resultEntity.put("allPeop",allPeop);
            resultEntity.put("signPeop",signPeop);
            resultEntity.put("dailyPeop",dailyPeop);
            resultEntity.put("latePeop",latePeop);
            resultEntity.put("earlyPeop",earlyPeop);
            resultData.put("entity",resultEntity);
            resultJson.put("resultCode",0);
            resultJson.put("resultData",resultData);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        return resultJson;
    }

    @PostMapping(value = "/userAttDayDetail")
    public JSONObject userAttDayDetail(@RequestParam(value = "userId") Integer userId,
                                    @RequestParam(value = "type") Integer type,
                                    @RequestParam(value = "pageNum") Integer pageNum,
                                    @RequestParam(value = "pageSize") Integer pageSize

    ){
        System.out.println("======into userAttDayDetail======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());

        List resultList = new ArrayList();
        try {
            InsUser insUser = userService.selectUserById(userId);
            InsDepartment userDept = institutionalService.selectDepartmentById(insUser.getDeptId());
            List<InsUser> userList = userService.selectDirectlyUser(userDept.getId());
            resultList.addAll(getAttDayDetail(userList,type));
            List<InsDepartment> dirDeptList = institutionalService.selectSubDepartment(userDept.getId());
            if(dirDeptList.size()>0){
                for (InsDepartment dirDept : dirDeptList){
                    List<InsUser> dirUserList = userService.selectDirectlyUser(dirDept.getId());
                    resultList.addAll(getAttDayDetail(dirUserList,type));

                    List<InsDepartment> subDeptList = institutionalService.selectSubDepartment(dirDept.getId());
                    if(subDeptList.size()>0){
                        for (InsDepartment subDept : subDeptList){
                            List<InsUser> subUserList = userService.selectDirectlyUser(subDept.getId());
                            resultList.addAll(getAttDayDetail(subUserList,type));
                        }
                    }
                }
            }
            switch (type){
                case 1:resultData.put("signPeopList",resultList);break;
                case 2:resultData.put("dailyPeopList",resultList);break;
                case 3:resultData.put("latePeopList",resultList);break;
                case 4:resultData.put("earlyPeopList",resultList);break;
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


    private Map<String,Integer> getAttNum(List<InsUser> insUserList,long startTime,long endTime) throws Exception{
        Map paramMap = new HashMap();
        Map<String,Integer> resultMap = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int latePeop = 0;
        int earlyPeop = 0;
        int shortagPeop = 0;
        int absenteeismPeop = 0;

        for(InsUser user : insUserList){
            paramMap.put("userId",user.getId());

            List<AttLeave> attLeaveList = attendanceService.selectLeave(paramMap);
            if(attLeaveList.size()==0){
                paramMap.put("type",1);
                paramMap.put("startTime",startTime);
                paramMap.put("endTime",endTime);
                List<Attendance> signAttendanceList = attendanceService.selectAttendance(paramMap);


            }
        }
        resultMap.put("latePeop",latePeop);
        resultMap.put("earlyPeop",earlyPeop);
        resultMap.put("shortagPeop",shortagPeop);
        resultMap.put("absenteeismPeop",absenteeismPeop);

        return resultMap;
    }

    @PostMapping(value = "userAttReport")
    public JSONObject userAttReport(@RequestParam(value = "userId") Integer userId,
                                    @RequestParam(value = "startTime") Long startTime,
                                    @RequestParam(value = "endTime") Long endTime
    ){
        System.out.println("======into userAttReport======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());

        int latePeop = 0;
        int earlyPeop = 0;
        int shortagPeop = 0;
        int absenteeismPeop = 0;

        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date sdate = new Date(startTime*1000);
//            Date edate = new Date(endTime*1000);
//            List<String> dateList = dateUtil.getTimeList(sdf.format(sdate),sdf.format(edate));

            InsUser insUser = userService.selectUserById(userId);
            InsDepartment userDept = institutionalService.selectDepartmentById(insUser.getDeptId());
            List<InsUser> userList = userService.selectDirectlyUser(userDept.getId());
            Map<String, Integer> numMap = getAttNum(userList,startTime,endTime);
            latePeop += numMap.get("latePeop");
            earlyPeop += numMap.get("earlyPeop");
            shortagPeop += numMap.get("shortagPeop");
            absenteeismPeop += numMap.get("absenteeismPeop");


            List<InsDepartment> dirDeptList = institutionalService.selectSubDepartment(userDept.getId());
            if(dirDeptList.size()>0){
                for (InsDepartment dirDept : dirDeptList){
                    List<InsUser> dirUserList = userService.selectDirectlyUser(dirDept.getId());
                    Map<String, Integer> dirNumMap = getAttNum(dirUserList,startTime,endTime);
                    latePeop += dirNumMap.get("latePeop");
                    earlyPeop += dirNumMap.get("earlyPeop");
                    shortagPeop += dirNumMap.get("shortagPeop");
                    absenteeismPeop += dirNumMap.get("absenteeismPeop");


                    List<InsDepartment> subDeptList = institutionalService.selectSubDepartment(dirDept.getId());
                    if(subDeptList.size()>0){
                        for (InsDepartment subDept : subDeptList){
                            List<InsUser> subUserList = userService.selectDirectlyUser(subDept.getId());
                            Map<String, Integer> subNumMap = getAttNum(subUserList,startTime,endTime);
                            latePeop += subNumMap.get("latePeop");
                            earlyPeop += subNumMap.get("earlyPeop");
                            shortagPeop += subNumMap.get("shortagPeop");
                            absenteeismPeop += subNumMap.get("absenteeismPeop");
                        }
                    }
                }
            }


            resultData.put("latePeop",latePeop);
            resultData.put("earlyPeop",earlyPeop);
            resultData.put("shortagPeop",shortagPeop);
            resultData.put("absenteeismPeop",absenteeismPeop);

            resultJson.put("resultCode",0);
            resultJson.put("resultData",resultData);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        return resultJson;
    }



    @PostMapping(value = "userAttDetail")
    public JSONObject userAttDetail(@RequestParam(value = "userId") Integer userId,
                                    @RequestParam(value = "startTime") Long startTime,
                                    @RequestParam(value = "endTime") Long endTime,
                                    @RequestParam(value = "type") Integer type,
                                    @RequestParam(value = "pageNum") Integer pageNum,
                                    @RequestParam(value = "pageSize") Integer pageSize
    ){
        System.out.println("======into userAttDetail======");
        JSONObject resultJson = new JSONObject(new LinkedHashMap<>());
        JSONObject resultData = new JSONObject(new LinkedHashMap<>());

        try {

            resultJson.put("resultCode",0);
            resultJson.put("resultData",resultData);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        return resultJson;
    }

    private Map<String,Integer> getAttDayNum(List<InsUser> insUserList){
        Map paramMap = new HashMap();
        Map<String,Integer> resultMap = new HashMap();
        int allPeop = 0;
        int signPeop = 0;
        int dailyPeop = 0;
        int latePeop = 0;
        int earlyPeop = 0;

        for(InsUser user : insUserList){
            paramMap.put("userId",user.getId());
            paramMap.put("paramTime",System.currentTimeMillis()/1000);
            List<AttLeave> attLeaveList = attendanceService.selectLeave(paramMap);
            if(attLeaveList.size()>0){
//                allPeop++;
            }

            paramMap.put("type",1);
            Attendance signAttendance = attendanceService.selectAttendanceForToday(paramMap);

            if(!StringUtils.isEmpty(signAttendance)){
                paramMap.put("type",2);
                Attendance outAttendance = attendanceService.selectAttendanceForToday(paramMap);
                if(signAttendance.getState() == 2){
                    latePeop++;
                }
                if(!StringUtils.isEmpty(outAttendance)){
                    if(outAttendance.getState() == 2){
                        earlyPeop++;
                    }
                }
                signPeop++;

            }else{
                dailyPeop++;
            }
            allPeop++;
        }
        resultMap.put("allPeop",allPeop);
        resultMap.put("signPeop",signPeop);
        resultMap.put("dailyPeop",dailyPeop);
        resultMap.put("latePeop",latePeop);
        resultMap.put("earlyPeop",earlyPeop);

        return resultMap;
    }

    private List getAttDayDetail(List<InsUser> insUserList,int type){
        Map paramMap = new HashMap();
        List resultList = new ArrayList();

        for(InsUser user : insUserList){
            paramMap.put("userId",user.getId());
            Map map = new LinkedHashMap();

            if(type == 1){
                paramMap.put("type",1);
                Attendance signAttendance = attendanceService.selectAttendanceForToday(paramMap);
                if(!StringUtils.isEmpty(signAttendance)){
                    map.put("id",user.getId());
                    map.put("name",user.getUserName());
                    map.put("status","已打卡");
                    InsDepartment userDept = institutionalService.selectDepartmentById(user.getDeptId());
                    map.put("deptName",userDept.getDeptName());
                    resultList.add(map);
                }
            }else if(type == 2){
                paramMap.put("type",1);
                Attendance signAttendance = attendanceService.selectAttendanceForToday(paramMap);
                if(StringUtils.isEmpty(signAttendance)){
                    map.put("id",user.getId());
                    map.put("name",user.getUserName());
                    paramMap.put("paramTime",System.currentTimeMillis()/1000);
                    List<AttLeave> attLeaveList = attendanceService.selectLeave(paramMap);
                    if(attLeaveList.size() > 0){
                        switch (attLeaveList.get(0).getType()){
                            case 2 :map.put("status","事假");break;
                            case 3 :map.put("status","病假");break;
                        }
                    }else{
                        map.put("status","未打卡");
                    }
                    InsDepartment userDept = institutionalService.selectDepartmentById(user.getDeptId());
                    map.put("deptName",userDept.getDeptName());
                    resultList.add(map);
                }
            }else if(type == 3){
                paramMap.put("type",1);
                Attendance signAttendance = attendanceService.selectAttendanceForToday(paramMap);
                if(!StringUtils.isEmpty(signAttendance)){
                    if(signAttendance.getState() == 2){
                        map.put("id",user.getId());
                        map.put("name",user.getUserName());
                        map.put("status","迟到");
                        InsDepartment userDept = institutionalService.selectDepartmentById(user.getDeptId());
                        map.put("deptName",userDept.getDeptName());
                        resultList.add(map);
                    }
                }
            }else if(type ==4){
                paramMap.put("type",2);
                Attendance outAttendance = attendanceService.selectAttendanceForToday(paramMap);
                if(!StringUtils.isEmpty(outAttendance)){
                    if(outAttendance.getState() ==2){
                        map.put("id",user.getId());
                        map.put("name",user.getUserName());
                        map.put("status","早退");
                        InsDepartment userDept = institutionalService.selectDepartmentById(user.getDeptId());
                        map.put("deptName",userDept.getDeptName());
                        resultList.add(map);
                    }
                }
            }
        }
        return resultList;
    }
}
