package com.religion.zhiyun.monitor.service;

import com.religion.zhiyun.monitor.entity.MonitroEntity;

import java.util.List;

public interface RmMonitroInfoService {
    List<MonitroEntity> allMonitro();

    void addMonitro(MonitroEntity monitroEntity);

    void updateMonitro(MonitroEntity monitroEntity);

    void deleteMonitro(String monitroId);

}
