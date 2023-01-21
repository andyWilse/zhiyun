package com.religion.zhiyun.event.service;

import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.utils.RespPageBean;

import java.util.List;
import java.util.Map;

public interface RmEventInfoService {

    void addEvent(EventEntity eventEntity);

    List<EventEntity> allEvent();

    void updateEvent(EventEntity eventEntity);

    void deleteEvent(int eventId);

    List<EventEntity> allEventByState();

    EventEntity getByEventId(String eventId);

    RespPageBean getByType(String eventType);
    List<Map<String,Object>> getAllNum();

    /**
     * 分页查询
     * @param page
     * @param size
     * @param accessNumber
     * @return
     */
    RespPageBean getEventsByPage(Integer page, Integer size, String accessNumber);

    /**
     * 手机端（预警查询）
     * @return
     */
    RespPageBean getEvents();
}
