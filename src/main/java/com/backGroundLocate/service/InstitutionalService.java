package com.backGroundLocate.service;

import com.backGroundLocate.entity.InsDepartment;
import com.backGroundLocate.entity.InsDepartmentRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface InstitutionalService {

    int createDepartment(InsDepartment authDepartment);

    List<InsDepartment> selectDepartment(Map paramMap);

    InsDepartment selectDepartmentById(int id);

    void updateDepartment(InsDepartment authDepartment);

    void deleteDepartment(int id);

    int createDeptRelation(InsDepartmentRelation insDepartmentRelation);

    List<InsDepartmentRelation> selectDeptRelation(Map paramMap);

    InsDepartmentRelation selectDeptRelationById(int id);

    InsDepartmentRelation selectDeptRelationByDeptId(@Param("deptId") int deptId);

    void updateDeptRelation(InsDepartmentRelation insDepartmentRelation);

    void deleteDeptRelation(int id);

    List<InsDepartment> selectSubDepartment(int parentDeptId);

}
