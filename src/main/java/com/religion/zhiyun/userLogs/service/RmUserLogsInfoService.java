package com.religion.zhiyun.userLogs.service;

import com.religion.zhiyun.userLogs.entity.LogsEntity;
import com.religion.zhiyun.utils.RespPageBean;

import java.util.List;

public interface RmUserLogsInfoService {

    List<LogsEntity> alllogs();
    void addlogs(LogsEntity logsEntity);
    void updatelogs(LogsEntity logsEntity);
    void deletelogs(String logsId);

    /**
     * 分页查询
     * @param page
     * @param size
     * @param userName
     * @return
     */
    RespPageBean getLogsByPage(Integer page, Integer size, String userName);

}
