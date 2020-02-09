package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.CarInfo;
import com.backGroundLocate.entity.InternalInfo;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.CarInfoService;
import com.backGroundLocate.service.InternalInfoService;
import com.backGroundLocate.service.UserInfoService;
import com.backGroundLocate.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private InternalInfoService internalInfoService;

    @Autowired
    private CarInfoService carInfoService;

    @RequestMapping(value = {"/login"})
    public JSONObject loginCheck(UserInfo paramInfo) {
        System.out.println("======into login======");
        System.out.println(paramInfo.getUserName());
        System.out.println(paramInfo.getPassword());
        String md5pwd = MD5Util.MD5Encode(paramInfo.getPassword());
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        try {
            UserInfo itemUser = userInfoService.selectUser(paramInfo);
            if (itemUser != null) {
                if (md5pwd.equals(itemUser.getPassword())) {
                    int userLevel = itemUser.getLevel();
                    UserInfo userInfo = new UserInfo();
                    userInfo.setId(itemUser.getId());
                    userInfo.setUserName(paramInfo.getUserName());
                    userInfo.setPassword(paramInfo.getPassword());
                    userInfo.setName(itemUser.getName());
                    userInfo.setLevel(userLevel);
                    userInfo.setDept(itemUser.getDept());
                    userInfo.setDeptName(itemUser.getDeptName());
                    if(userLevel==2){
                    }else if(userLevel==3){
                        ArrayList empList = new ArrayList();
                        ArrayList carList = new ArrayList();
                        List<InternalInfo> internalInfoList = internalInfoService.selectInternalListByDeptId(itemUser.getDept());
                        for (int i=0;i<internalInfoList.size();i++){
                            List<UserInfo> userInfoList = userInfoService.selectUserListByInternalId(internalInfoList.get(i).getId());
                            List<CarInfo> carInfoList = carInfoService.selectCarListByInternalId(internalInfoList.get(i).getId());
                            JSONObject internalInfoEmp = new JSONObject();
                            JSONObject internalInfoCar = new JSONObject();
                            JSONArray empGroup = new JSONArray();
                            JSONArray carGroup = new JSONArray();
                            for (int j=0;j<userInfoList.size();j++){
                                JSONObject internalUser = new JSONObject();
                                internalUser.put("id",userInfoList.get(j).getId());
                                internalUser.put("name",userInfoList.get(j).getName());
                                empGroup.add(internalUser);
                            }
                            for (int j=0;j<carInfoList.size();j++){
                                JSONObject internalCar = new JSONObject();
                                internalCar.put("id",carInfoList.get(j).getId());
                                internalCar.put("name",carInfoList.get(j).getName());
                                internalCar.put("typeId",carInfoList.get(j).getTypeId());
                                internalCar.put("typeName",carInfoList.get(j).getTypeName());
                                carGroup.add(internalCar);
                            }
                            internalInfoEmp.put("internalId",internalInfoList.get(i).getId());
                            internalInfoEmp.put("internalName",internalInfoList.get(i).getInternalName());
                            internalInfoEmp.put("empGroup",empGroup);
                            empList.add(internalInfoEmp);
                            internalInfoCar.put("internalId",internalInfoList.get(i).getId());
                            internalInfoCar.put("internalName",internalInfoList.get(i).getInternalName());
                            internalInfoCar.put("carGroup",carGroup);
                            carList.add(internalInfoCar);
                        }
                        userInfo.setEmpList(empList);
                        userInfo.setCarList(carList);
                    }
                    resultData.put("userInfo",userInfo);
                    resultJson.put("resultCode",0);
                    resultJson.put("resultData",resultData);
                } else {
                    resultJson.put("resultCode",1);
                    resultJson.put("resultMessage","密码错误");
                }
            } else {
                resultJson.put("resultCode",1);
                resultJson.put("resultMessage","用户不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }
        System.out.println("resultJson======>"+resultJson);
        return resultJson;
    }
}
