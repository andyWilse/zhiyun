package com.religion.zhiyun.monitor.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface MonitorBaseMapper {

    /**根据设备编号获取场所**/
    String getVenue(String channelId);

    /**获取所有监控**/
    List<Map<String,Object>> getAllMonitors();
}
