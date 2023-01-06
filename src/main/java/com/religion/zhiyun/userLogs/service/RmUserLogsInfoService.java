package com.religion.zhiyun.userLogs.service;

import com.religion.zhiyun.userLogs.entity.LogsEntity;

import java.util.List;

public interface RmUserLogsInfoService {

    List<LogsEntity> alllogs();
    void addlogs(LogsEntity logsEntity);
    void updatelogs(LogsEntity logsEntity);
    void deletelogs(String logsId);
}
