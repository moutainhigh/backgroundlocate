package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.AuthAccount;
import com.backGroundLocate.entity.AuthRole;
import com.backGroundLocate.entity.InsUser;
import com.backGroundLocate.service.*;
import com.backGroundLocate.util.MD5Util;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@Api(tags = "权限管理接口")
@RequestMapping(value = "/authManage")
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    /**
     * APP账号登陆
     * @param account
     * @param password
     * @return
     */
    @PostMapping(value = {"/login"})
    public JSONObject login(@RequestParam(value = "account") String account,@RequestParam(value = "password") String password) {
        System.out.println("======into login======");
        String md5pwd = MD5Util.MD5Encode(password);
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        try {
            AuthAccount authAccount = authService.selectByAccount(account);
            if (!StringUtils.isEmpty(authAccount)) {
                AuthRole authRole = authService.selectRoleById(authAccount.getRoleId());
                InsUser insUser = userService.selectUserById(authAccount.getUserId());
                if (md5pwd.equals(authAccount.getPassword())) {
                    JSONObject userInfoJson = new JSONObject(new LinkedHashMap<>());
                    userInfoJson.put("userId",insUser.getId());
                    userInfoJson.put("userName",insUser.getUserName());
                    userInfoJson.put("accountId",authAccount.getId());
                    userInfoJson.put("type",authAccount.getType());
                    userInfoJson.put("roleName",authRole.getRoleName());
                    userInfoJson.put("roleLevel",authRole.getRoleLevel());
                    userInfoJson.put("deptId",insUser.getDeptId());
                    userInfoJson.put("deptName",insUser.getDeptName());


                    authAccount.setLastLoginTime(System.currentTimeMillis()/1000);
                    authService.updateAccount(authAccount);

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
     * @param accountId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @PostMapping(value = {"/modifyPassword"})
    public JSONObject modifyPassword(@RequestParam(value = "accountId") Integer accountId,
                                     @RequestParam(value = "oldPassword") String oldPassword,
                                     @RequestParam(value = "newPassword") String newPassword){
        System.out.println("======into modifyPassword======");
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();

        try {
            AuthAccount authAccount = authService.selectAccountById(accountId);
            String oldpwd = MD5Util.MD5Encode(oldPassword);
            if (oldpwd.equals(authAccount.getPassword())){
                authAccount.setPassword(MD5Util.MD5Encode(newPassword));
                authService.updateAccount(authAccount);
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
