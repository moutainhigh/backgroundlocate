package com.backGroundLocate.mapper;

import com.backGroundLocate.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DepartmentMapper {

    List<Department> selectDepartment(Department department);
}
