package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.InsDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface InsDepartmentMapper {

    int createDepartment(InsDepartment authDepartment);

    List<InsDepartment> selectDepartment(Map paramMap);

    InsDepartment selectDepartmentById(@Param("id") int id);

    void updateDepartment(InsDepartment authDepartment);

    void deleteDepartment(@Param("id") int id);

    List<InsDepartment> selectSubDepartment(@Param("parentDeptId") int parentDeptId);

}
