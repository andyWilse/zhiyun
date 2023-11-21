package com.religion.zhiyun.monitor.dao;

import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface MonitorBaseMapper {

    /**根据设备编号获取场所**/
    String getVenue(@Param("channelId") String channelId);

    /**获取所有监控**/
    List<Map<String,Object>> getAllMonitors();

    /**获取所有监控**/
    List<Map<String,Object>> queryMonitors(@Param("vo") ParamsVo vo);

}
