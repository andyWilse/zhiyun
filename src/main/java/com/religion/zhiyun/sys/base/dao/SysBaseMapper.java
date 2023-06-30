package com.religion.zhiyun.sys.base.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysBaseMapper {
    /**获取开关状态**/
    String getOpenState(String sysEnNm);
}
