package com.religion.zhiyun.event.service;

import com.religion.zhiyun.event.entity.EventEntity;

import java.util.List;

public interface RmEventInfoService {

    void addEvent(EventEntity eventEntity);

    List<EventEntity> allEvent();

    void updateEvent(EventEntity eventEntity);

    void deleteEvent(String eventId);
}
