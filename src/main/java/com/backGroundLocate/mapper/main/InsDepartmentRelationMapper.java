package com.backGroundLocate.mapper.main;

import com.backGroundLocate.entity.InsDepartmentRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface InsDepartmentRelationMapper {

    int createDeptRelation(InsDepartmentRelation insDepartmentRelation);

    List<InsDepartmentRelation> selectDeptRelation(Map paramMap);

    InsDepartmentRelation selectDeptRelationById(@Param("id") int id);

    void updateDeptRelation(InsDepartmentRelation insDepartmentRelation);

    void deleteDeptRelation(@Param("id") int id);

    InsDepartmentRelation selectDeptRelationByDeptId(@Param("deptId") int deptId);

}
