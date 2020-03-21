package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.Department;
import com.backGroundLocate.mapper.DepartmentMapper;
import com.backGroundLocate.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> selectDepartment(Department department) {
        return departmentMapper.selectDepartment(department);
    }
}
