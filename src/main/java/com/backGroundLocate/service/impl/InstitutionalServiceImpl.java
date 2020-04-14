package com.backGroundLocate.service.impl;

import com.backGroundLocate.mapper.main.InsDepartmentMapper;
import com.backGroundLocate.mapper.main.InsDepartmentRelationMapper;
import com.backGroundLocate.entity.InsDepartment;
import com.backGroundLocate.entity.InsDepartmentRelation;
import com.backGroundLocate.service.InstitutionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InstitutionalServiceImpl implements InstitutionalService {

    @Autowired
    private InsDepartmentMapper insDepartmentMapper;

    @Autowired
    private InsDepartmentRelationMapper insDepartmentRelationMapper;


    @Override
    public int createDepartment(InsDepartment department) {
        return insDepartmentMapper.createDepartment(department);
    }

    @Override
    public List<InsDepartment> selectDepartment(Map paramMap) {
        return insDepartmentMapper.selectDepartment(paramMap);
    }

    @Override
    public InsDepartment selectDepartmentById(int id) {
        return insDepartmentMapper.selectDepartmentById(id);
    }

    @Override
    public void updateDepartment(InsDepartment department) {
        insDepartmentMapper.updateDepartment(department);
    }

    @Override
    public void deleteDepartment(int id) {
        insDepartmentMapper.deleteDepartment(id);
    }

    @Override
    public int createDeptRelation(InsDepartmentRelation insDepartmentRelation) {
        return insDepartmentRelationMapper.createDeptRelation(insDepartmentRelation);
    }

    @Override
    public List<InsDepartmentRelation> selectDeptRelation(Map paramMap) {
        return insDepartmentRelationMapper.selectDeptRelation(paramMap);
    }

    @Override
    public InsDepartmentRelation selectDeptRelationById(int id) {
        return insDepartmentRelationMapper.selectDeptRelationById(id);
    }

    @Override
    public InsDepartmentRelation selectDeptRelationByDeptId(int deptId) {
        return insDepartmentRelationMapper.selectDeptRelationByDeptId(deptId);
    }

    @Override
    public void updateDeptRelation(InsDepartmentRelation insDepartmentRelation) {
        insDepartmentRelationMapper.updateDeptRelation(insDepartmentRelation);
    }

    @Override
    public void deleteDeptRelation(int id) {
        insDepartmentRelationMapper.deleteDeptRelation(id);
    }

    @Override
    public List<InsDepartment> selectSubDepartment(int parentDeptId) {
        return insDepartmentMapper.selectSubDepartment(parentDeptId);
    }


}