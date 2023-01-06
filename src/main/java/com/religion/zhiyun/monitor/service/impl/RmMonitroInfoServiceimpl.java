package com.religion.zhiyun.monitor.service.impl;

import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.monitor.service.RmMonitroInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RmMonitroInfoServiceimpl implements RmMonitroInfoService {
    @Autowired
    private RmMonitroInfoMapper rmMonitroInfoMapper;

    @Override
    public List<MonitroEntity> allMonitro() {
        return rmMonitroInfoMapper.allMonitro();
    }

    @Override
    public void addMonitro(MonitroEntity monitroEntity) {
        rmMonitroInfoMapper.addMonitro(monitroEntity);
    }

    @Override
    public void updateMonitro(MonitroEntity monitroEntity) {
        rmMonitroInfoMapper.updateMonitro(monitroEntity);
    }

    @Override
    public void deleteMonitro(String monitroId) {
        rmMonitroInfoMapper.deleteMonitro(monitroId);
    }
}
