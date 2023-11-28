package com.religion.zhiyun.event.dao;

import com.religion.zhiyun.event.entity.NotifiedEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;


@Mapper
@Repository
public interface EventNotifiedMapper {

    /**
     * 新增
     * @param notifiedEntity
     */
    void addNotified(NotifiedEntity notifiedEntity);

    /**
     * 更新
     * @param eventId
     * @return
     */
    void updateNotifiedFlag(String eventId, String ymdHms,String notifiedFlag);
    void updateNotifiedUser(String eventId, String ymdHms,String notifiedUser);

    /**
     * 获取电话通知人
     */
    Map<String,String> getNotified();

}
