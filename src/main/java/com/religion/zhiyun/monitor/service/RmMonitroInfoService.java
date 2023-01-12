package com.religion.zhiyun.monitor.service;

import com.religion.zhiyun.monitor.entity.MonitroEntity;

import java.util.List;
import java.util.Map;

public interface RmMonitroInfoService {
    List<MonitroEntity> allMonitro();

    void addMonitro(MonitroEntity monitroEntity);

    void updateMonitro(MonitroEntity monitroEntity);

    void deleteMonitro(String monitroId);
    List<Map<String,Object>> getAllNum();
    List<MonitroEntity> getMonitorByState(String state);
    List<MonitroEntity> getMonitorByVenuesId(String state);
}
