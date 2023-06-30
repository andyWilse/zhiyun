package com.religion.zhiyun.monitor.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MonitorSmokerMapper {

    /**根据设备编号获取场所**/
    String getVenue(@Param("smokerId")String smokerId);
}
