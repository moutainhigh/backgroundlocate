package com.backGroundLocate.mapper;

import com.backGroundLocate.entity.InternalInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface InternalInfoMapper {

    List<InternalInfo> selectInternalListByDeptId(@Param("deptId") int deptId);
}
