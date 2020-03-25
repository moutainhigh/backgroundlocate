package com.backGroundLocate.service.impl;

import com.backGroundLocate.entity.Department;
import com.backGroundLocate.mapper.main.DepartmentMapper;
import com.backGroundLocate.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> selectDepartmentList(Department department) {
        return departmentMapper.selectDepartmentList(department);
    }

    @Override
    public Department selectDepartmentByPrimary(int id) {
        return departmentMapper.selectDepartmentByPrimary(id);
    }
}
