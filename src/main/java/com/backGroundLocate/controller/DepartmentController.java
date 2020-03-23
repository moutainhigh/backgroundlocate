package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSONArray;
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

import java.util.ArrayList;
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
        try {
            UserInfo itemUser = userInfoService.selectUserById(Integer.valueOf(userId));
            Department conDept = new Department();
            conDept.setId(itemUser.getDeptId());

            List<Department> departmentList = departmentService.selectDepartment(conDept);
            List<UserInfo> userList = new ArrayList<UserInfo>();
            List<CarInfo> carList = new ArrayList<CarInfo>();
            //账号所在部门
            if(departmentList.size()>0){
                JSONObject deptInfo = new JSONObject();
                for(Department dept : departmentList){
                    System.out.println("一");
                    deptInfo = JSONObject.parseObject(dept.toString());
                    userList = userInfoService.selectUserListByDept(dept.getId());
                    deptInfo.put("currentUserList",JSONArray.toJSON(userList));
                    System.out.println(JSONObject.toJSONString(userList));
                    if(dept.getDeptLevel()<3){
                        JSONArray directlyList = new JSONArray();
                        JSONObject directlyDept = new JSONObject();

                        //账号所在部门--直辖部门
                        conDept = new Department();
                        conDept.setParentId(dept.getId());
                        departmentList = departmentService.selectDepartment(conDept);
                        System.out.println(JSONObject.toJSONString(departmentList));

                        for(Department dept1 : departmentList){
                            System.out.println("二");
                            directlyDept = JSONObject.parseObject(dept1.toString());
                            if(dept.getDeptLevel()==1){
                                userList = userInfoService.selectUserListByDept(dept1.getId());
                                directlyDept.put("directlyUserList",JSONArray.toJSON(userList));
                                System.out.println(JSONObject.toJSONString(userList));
                                //账号所在部门--直辖部门--下属部门
                                conDept = new Department();
                                conDept.setParentId(dept1.getId());
                                departmentList = departmentService.selectDepartment(conDept);
                                System.out.println(JSONObject.toJSONString(departmentList));
                                JSONArray administerUserDeptList = new JSONArray();
                                JSONArray administerCarDeptList = new JSONArray();
                                JSONObject administerDept;

                                for (Department dept2:departmentList){
                                    System.out.println("三");
                                    administerDept = JSONObject.parseObject(dept2.toString());
                                    if(dept2.getDeptType()==3){
                                        userList = userInfoService.selectUserListByDept(dept2.getId());
                                        System.out.println(JSONObject.toJSONString(userList));
                                        administerDept.put("userList",JSONArray.toJSON(userList));
                                        administerUserDeptList.add(administerDept);

                                    }else if(dept2.getDeptType()==4){
                                        carList = carInfoService.selectCarListByDept(dept2.getId());
                                        System.out.println(JSONObject.toJSONString(carList));
                                        administerDept.put("carList",JSONArray.toJSON(carList));
                                        administerCarDeptList.add(administerDept);

                                    }
                                    directlyDept.put("administerUserDeptList",administerUserDeptList);
                                    directlyDept.put("administerCarDeptList",administerCarDeptList);

                                }
                                directlyList.add(directlyDept);
                            }else {
                                if(dept1.getDeptType()==3){
                                    userList = userInfoService.selectUserListByDept(dept1.getId());
                                    directlyDept.put("directlyUserList",JSONArray.toJSON(userList));
                                    System.out.println(JSONObject.toJSONString(userList));
                                }else if(dept1.getDeptType()==4){
                                    carList = carInfoService.selectCarListByDept(dept1.getId());
                                    System.out.println(JSONObject.toJSONString(carList));
                                    directlyDept.put("directlyCarList",JSONArray.toJSON(carList));
                                }
                                directlyList.add(directlyDept);
                            }
                        }
                        deptInfo.put("directlyList",directlyList);
                    }
                }
                System.out.println(deptInfo);
                resultJson.put("resultCode",0);
                resultData.put("deptInfo",deptInfo);
                resultJson.put("resultData",resultData);
            }
        }catch (Exception e){
            e.printStackTrace();
            resultJson.put("resultCode",1);
            resultJson.put("resultMessage",e.getMessage());
        }

        return resultJson;
    }
}