package com.backGroundLocate.entity;

import java.util.List;

public class UserInfo {

    private int id;
    private String userName;
    private String password;
    private String name;
    private int level;
    private int dept;
    private String deptName;
    private int internalId;
    private String internalName;
//    private List empList;
//    private List carList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDept() {
        return dept;
    }

    public void setDept(int dept) {
        this.dept = dept;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public int getInternalId() {
        return internalId;
    }

    public void setInternalId(int internalId) {
        this.internalId = internalId;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

//    public List getEmpList() {
//        return empList;
//    }
//
//    public void setEmpList(List empList) {
//        this.empList = empList;
//    }
//
//    public List getCarList() {
//        return carList;
//    }
//
//    public void setCarList(List carList) {
//        this.carList = carList;
//    }
}
