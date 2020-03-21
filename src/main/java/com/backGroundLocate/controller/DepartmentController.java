package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.entity.CarInfo;
import com.backGroundLocate.entity.Department;
import com.backGroundLocate.entity.UserInfo;
import com.backGroundLocate.service.CarInfoService;
import com.backGroundLocate.service.DepartmentService;
import com.backGroundLocate.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DepartmentController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/selectDepartmentList")
    public JSONObject selectDepartmentList(@RequestParam(value = "userId") String userId){
        JSONObject resultJson = new JSONObject();
        JSONObject resultData = new JSONObject();
        UserInfo itemUser = userInfoService.selectUserById(Integer.valueOf(userId));
        Department conDept = new Department();
        conDept.setId(itemUser.getDeptId());

        List<Department> departmentList = departmentService.selectDepartment(conDept);
        List<UserInfo> userList;
        List<CarInfo> carList;
        //账号所在部门
        if(departmentList.size()>0){

            for(Department dept : departmentList){
                System.out.println("一");
                userList = userInfoService.selectUserListByDept(dept.getId());
                System.out.println(JSONObject.toJSONString(userList));
                if(dept.getDeptLevel()<3){
                    //账号所在部门--直辖部门
                    conDept = new Department();
                    conDept.setParentId(dept.getId());
                    departmentList = departmentService.selectDepartment(conDept);
                    System.out.println(JSONObject.toJSONString(departmentList));

                    for(Department dept1:departmentList){
                        System.out.println("二");
                        if(dept.getDeptLevel()==1){
                            userList = userInfoService.selectUserListByDept(dept1.getId());
                            System.out.println(JSONObject.toJSONString(userList));
                            //账号所在部门--直辖部门--下属部门
                            conDept = new Department();
                            conDept.setParentId(dept1.getId());
                            departmentList = departmentService.selectDepartment(conDept);
                            System.out.println(JSONObject.toJSONString(departmentList));

                            for (Department dept2:departmentList){
                                System.out.println("三");
                                if(dept2.getDeptType()==3){
                                    userList = userInfoService.selectUserListByDept(dept2.getId());
                                    System.out.println(JSONObject.toJSONString(userList));
                                }else if(dept2.getDeptType()==4){
                                    carList = carInfoService.selectCarListByDept(dept2.getId());
                                    System.out.println(JSONObject.toJSONString(carList));
                                }
                            }
                        }else {
                            if(dept1.getDeptType()==3){
                                userList = userInfoService.selectUserListByDept(dept1.getId());
                                System.out.println(JSONObject.toJSONString(userList));
                            }else if(dept1.getDeptType()==4){
                                carList = carInfoService.selectCarListByDept(dept1.getId());
                                System.out.println(JSONObject.toJSONString(carList));
                            }
                        }
                    }
                }
            }
        }
        return resultJson;
    }
}