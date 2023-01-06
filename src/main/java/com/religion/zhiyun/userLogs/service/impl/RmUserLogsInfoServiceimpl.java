package com.religion.zhiyun.userLogs.service.impl;

import com.religion.zhiyun.userLogs.dao.RmUserLogsInfoMapper;
import com.religion.zhiyun.userLogs.entity.LogsEntity;
import com.religion.zhiyun.userLogs.service.RmUserLogsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RmUserLogsInfoServiceimpl implements RmUserLogsInfoService {
    @Autowired
    private RmUserLogsInfoMapper rmUserLogsInfoMapper;

    @Override
    public List<LogsEntity> alllogs() {
        return rmUserLogsInfoMapper.alllogs();
    }

    @Override
    public void addlogs(LogsEntity logsEntity) {
        rmUserLogsInfoMapper.addlogs(logsEntity);
    }

    @Override
    public void updatelogs(LogsEntity logsEntity) {
        rmUserLogsInfoMapper.updatelogs(logsEntity);
    }

    @Override
    public void deletelogs(String logsId) {
        rmUserLogsInfoMapper.deletelogs(logsId);
    }
}
