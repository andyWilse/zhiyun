package com.religion.zhiyun.event.dao;


import com.religion.zhiyun.event.entity.EventEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RmEventInfoMapper {
    /**
     * 新增
     * @param eventEntity
     */
    void addEvent(EventEntity eventEntity);

    //查询
    List<EventEntity> allEvent();

    /**
     * 修改
     * @param eventEntity
     */
    void updateEvent(EventEntity eventEntity);

    /**
     * 删除
     * @param eventId
     */
    void deleteEvent(String eventId);

    /**
     * 根据未完成的状态展示
     * @return
     */
    List<EventEntity> allEventByState();

    /**
     * 根据事件id查询事件、场所、监控设备信息
     * @param eventId
     * @return
     */
    EventEntity getByEventId(String eventId);

    /**
     * 根据类型查看
     * @param eventType
     * @return
     */
    List<EventEntity> getByType(String eventType);

    List<Map<String,Object>> getAllNum();
}
