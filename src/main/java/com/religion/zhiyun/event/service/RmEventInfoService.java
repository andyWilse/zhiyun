package com.religion.zhiyun.event.service;

import com.religion.zhiyun.event.entity.EventEntity;

import java.util.List;
import java.util.Map;

public interface RmEventInfoService {

    void addEvent(EventEntity eventEntity);

    List<EventEntity> allEvent();

    void updateEvent(EventEntity eventEntity);

    void deleteEvent(String eventId);

    List<EventEntity> allEventByState();

    EventEntity getByEventId(String eventId);

    List<EventEntity> getByType(String eventType);
    List<Map<String,Object>> getAllNum();
}
