package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.CarInfoService;
import com.backGroundLocate.service.UserInfoService;
import com.backGroundLocate.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CarInfoService carInfoService;

    @RequestMapping(value = {"/login"})
    public JSONObject loginCheck(@RequestParam(value = "userName") String userName,@RequestParam(value = "password") String password) {
        System.out.println("======into login======");
        String md5pwd = MD5Util.MD5Encode(password);
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        try {
            UserInfo paramUser = new UserInfo();
            paramUser.setUserName(userName);
            UserInfo itemUser = userInfoService.selectUser(paramUser);
            if (itemUser != null) {
                if (md5pwd.equals(itemUser.getPassword())) {

                    JSONObject userInfoJson = new JSONObject();
                    userInfoJson.put("level",itemUser.getLevel());
                    userInfoJson.put("dept",itemUser.getDeptId());
                    userInfoJson.put("deptName",itemUser.getDeptName());
                    userInfoJson.put("name",itemUser.getName());
                    userInfoJson.put("id",itemUser.getId());
                    resultData.put("userInfo",userInfoJson);
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
