package com.religion.zhiyun.monitor.dao;

import com.religion.zhiyun.monitor.entity.MonitorStatEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MonitorStatMapper {
    /*新增*/
    void add(MonitorStatEntity ent);
}
