package com.religion.zhiyun.event.service.impl;

import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RmEventInfoServiceimpl implements RmEventInfoService {
    @Autowired
    private RmEventInfoMapper rmEventInfoMapper;

    @Override
    public void addEvent(EventEntity eventEntity) {
        rmEventInfoMapper.addEvent(eventEntity);
    }

    @Override
    public List<EventEntity> allEvent() {
        return rmEventInfoMapper.allEvent();
    }

    @Override
    public void updateEvent(EventEntity eventEntity) {
        rmEventInfoMapper.updateEvent(eventEntity);
    }

    @Override
    public void deleteEvent(String eventId) {
        rmEventInfoMapper.deleteEvent(eventId);
    }

    @Override
    public List<EventEntity> allEventByState() {
        return rmEventInfoMapper.allEventByState();
    }

    @Override
    public EventEntity getByEventId(String eventId) {
        return rmEventInfoMapper.getByEventId(eventId);
    }

    @Override
    public List<EventEntity> getByType(String eventType) {
        return rmEventInfoMapper.getByType(eventType);
    }

    @Override
    public List<Map<String, Object>> getAllNum() {
        return rmEventInfoMapper.getAllNum();
    }

    @Override
    public RespPageBean getEventsByPage(Integer page, Integer size, String accessNumber) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<VenuesEntity> dataList=rmEventInfoMapper.getEventsByPage(page,size,accessNumber);
        Object[] objects = dataList.toArray();
        Long total=rmEventInfoMapper.getTotal();
        RespPageBean bean = new RespPageBean();
        bean.setDatas(objects);
        bean.setTotal(total);
        return bean;
    }
}