package com.religion.zhiyun.userLogs.dao;

import com.religion.zhiyun.userLogs.entity.LogsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RmUserLogsInfoMapper {
    List<LogsEntity> alllogs();
    void addlogs(LogsEntity logsEntity);
    void updatelogs(LogsEntity logsEntity);
    void deletelogs(String logsId);

}
