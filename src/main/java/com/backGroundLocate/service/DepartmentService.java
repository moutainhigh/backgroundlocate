package com.backGroundLocate.service;

import com.backGroundLocate.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentService {

    List<Department> selectDepartmentList(Department department);

    Department selectDepartmentByPrimary(int id);

    Department selectDepartment(Department department);
}
