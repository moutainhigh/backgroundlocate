package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DepartmentMapper {

    List<Department> selectDepartmentList(Department department);

    Department selectDepartmentByPrimary(@Param(value = "id") int id);

    Department selectDepartment(Department department);
}
