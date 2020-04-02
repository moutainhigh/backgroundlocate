package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.CarInfoService;
import com.backGroundLocate.service.UserInfoService;
import com.backGroundLocate.util.MD5Util;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@Api(tags = "登录接口")
@RestController
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CarInfoService carInfoService;

    /**
     * APP账号登陆
     * @param userName
     * @param password
     * @return
     */
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

                    JSONObject userInfoJson = new JSONObject(new LinkedHashMap<>());
                    userInfoJson.put("level",itemUser.getLevel());
                    userInfoJson.put("dept",itemUser.getDeptId());
                    userInfoJson.put("deptName",itemUser.getDeptName());
                    userInfoJson.put("name",itemUser.getName());
                    userInfoJson.put("id",itemUser.getId());
                    userInfoJson.put("type",itemUser.getType());
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

    /**
     * 账号密码修改
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = {"/modifyPassword"})
    public JSONObject modifyPassword(@RequestParam(value = "userId") String userId,
                                     @RequestParam(value = "oldPassword") String oldPassword,
                                     @RequestParam(value = "newPassword") String newPassword){
        System.out.println("======into modifyPassword======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();

        try {
            UserInfo userInfo = userInfoService.selectUserById(Integer.parseInt(userId));
            String oldpwd = MD5Util.MD5Encode(oldPassword);
            if (oldpwd.equals(userInfo.getPassword())){
                userInfo.setPassword(MD5Util.MD5Encode(newPassword));
                userInfoService.updateUserInfo(userInfo);

            }else{
                resultJson.put("resultCode",1);
                resultJson.put("resultMessage","原始密码错误,无法修改");
                return resultJson;
            }
            resultJson.put("resultCode",0);
            resultData.put("resultStatus","success");
            resultJson.put("resultData",resultData);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return resultJson;
    }
}
