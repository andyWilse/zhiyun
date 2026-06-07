package com.religion.zhiyun.sys.log.dao;

import com.religion.zhiyun.sys.log.entity.AppmetricLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

@Repository
@Mapper
public interface AppmetricLogMapper {
    /**
     * 新增
     * @param appmetricLogEntity
     * @return
     */
    int add(AppmetricLogEntity appmetricLogEntity);

    /**
     * 获取日增预警数
     * @param warnTime
     * @return
     */
    int getEventAdd(@Param("warnTime") LocalDateTime warnTime);

    /**
     * 获取预警处理
     * @param handleTime
     * @return
     */
    HashMap<String, BigDecimal> getTask(@Param("handleTime") LocalDateTime handleTime);

    /**
     * 获取通知人
     * @param notifyTime
     * @return
     */
    String getNotify(@Param("notifyTime") LocalDateTime notifyTime);

}
