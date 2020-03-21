package com.backGroundLocate.service;

import com.backGroundLocate.entity.Department;

import java.util.List;

public interface DepartmentService {

    List<Department> selectDepartment(Department department);
}
