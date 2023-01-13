package com.religion.zhiyun.userLogs.dao;

import com.religion.zhiyun.userLogs.entity.LogsEntity;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RmUserLogsInfoMapper {
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

    List<VenuesEntity> getLogsByPage(@Param("page") Integer page, @Param("size") Integer size, @Param("userName") String userName);

    /**
     * 获取总条数
     * @return
     */
    Long getTotal();
}
