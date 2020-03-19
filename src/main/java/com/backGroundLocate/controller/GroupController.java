package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.InternalInfo;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.CarInfoService;
import com.backGroundLocate.service.InternalInfoService;
import com.backGroundLocate.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GroupController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private InternalInfoService internalInfoService;

    @RequestMapping(value = "/selectGroupList")
    public JSONObject selectGroupList(@RequestParam(value = "user_id") String user_id,@RequestParam(value = "type") String type){
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        UserInfo itemUser = userInfoService.selectUserById(Integer.valueOf(user_id));
        List<InternalInfo> internalInfoList = internalInfoService.selectInternalListByDeptId(itemUser.getDept());
        for(InternalInfo i : internalInfoList){

        }
        return resultJson;
    }
}
