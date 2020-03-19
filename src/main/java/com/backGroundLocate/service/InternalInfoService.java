package com.backGroundLocate.service;

import com.backGroundLocate.entity.InternalInfo;

import java.util.List;

public interface InternalInfoService {

    List<InternalInfo> selectInternalListByDeptId(int deptId);
}
