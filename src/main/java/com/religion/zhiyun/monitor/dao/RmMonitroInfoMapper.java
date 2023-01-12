package com.religion.zhiyun.monitor.dao;

import com.religion.zhiyun.monitor.entity.MonitroEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RmMonitroInfoMapper {
    List<MonitroEntity> allMonitro();

    void addMonitro(MonitroEntity monitroEntity);

    void updateMonitro(MonitroEntity monitroEntity);

    void deleteMonitro(String monitroId);
    List<Map<String,Object>> getAllNum();

    List<MonitroEntity> getMonitorByState(String state);

    List<MonitroEntity> getMonitorByVenuesId(String state);
}
