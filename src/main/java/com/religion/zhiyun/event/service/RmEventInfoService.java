package com.religion.zhiyun.event.service;

import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.OutInterfaceResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.List;
import java.util.Map;

public interface RmEventInfoService {

    OutInterfaceResponse addEvent(String eventEntity);

    List<EventEntity> allEvent();

    void updateEvent(EventEntity eventEntity);

    void deleteEvent(int eventId);

    List<EventEntity> allEventByState();

    EventEntity getByEventId(String eventId);

    AppResponse getByType(Integer page, Integer size, String eventType);

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

    /**
     * 接收烟感器、燃气探测传输的数据
     * @param eventEntity
     */
    OutInterfaceResponse addEventByNB(String eventEntity);


}
