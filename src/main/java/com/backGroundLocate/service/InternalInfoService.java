package com.backGroundLocate.service;

import com.backGroundLocate.entity.InternalInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface InternalInfoService {

    List<InternalInfo> selectInternalListByDeptId(int deptId);
}
