package com.religion.zhiyun.schedule.service.impl;

import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.schedule.service.SchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulesServiceImpl implements SchedulesService {

    @Autowired
    private RmEventInfoMapper rmEventInfoMapper;


    @Override
    public void UrgentAutoReport() {
        //查询十分钟没人解除预警且无人上报，
        List<EventEntity> urgentEvent = rmEventInfoMapper.findFillEvent("","","-10");
        System.out.println("查询十分钟没人解除预警且无人上报===="+urgentEvent.size());
        if(null!=urgentEvent && urgentEvent.size()>0){
            // 发起人自动上报
            for(int i=0;i<urgentEvent.size();i++){
                EventEntity event = urgentEvent.get(i);
            }
        }
    }

    @Override
    public void CommonAutoFill() {
        //十天后预警无人处理，则短信通知
        List<EventEntity> urgentEvent = rmEventInfoMapper.findFillEvent("-1","","");
        System.out.println("十天后预警无人处理，则短信通知==="+urgentEvent.size());
    }
}
