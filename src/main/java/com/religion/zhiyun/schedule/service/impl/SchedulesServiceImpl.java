package com.religion.zhiyun.schedule.service.impl;

import com.religion.zhiyun.event.dao.EventNotifiedMapper;
import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.interfaces.entity.huawei.FeeInfo;
import com.religion.zhiyun.schedule.service.SchedulesService;
import com.religion.zhiyun.sys.log.dao.AppmetricLogMapper;
import com.religion.zhiyun.sys.log.entity.AppmetricLogEntity;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.enums.CallEnums;
import com.religion.zhiyun.utils.sms.call.VoiceCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchedulesServiceImpl implements SchedulesService {

    @Autowired
    private RmEventInfoMapper rmEventInfoMapper;
    @Autowired
    private EventNotifiedMapper eventNotifiedMapper;
    @Autowired
    private AppmetricLogMapper appmetricLogMapper;

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

    @Override
    public void warnCallReport() {
        Map<String, String> notified = eventNotifiedMapper.getNotified();
        if(null!=notified){
            String manegers = notified.get("manegers");
            String users = notified.get("users");
            String arr = notified.get("arr");
            String nm = notified.get("nm");
            String event = notified.get("event");
            String relEventId = notified.get("eventId");
            //管理
            if(!GeneTool.isEmpty(manegers)){
                String[] ma = manegers.split(",");
                for(int i=0;i<ma.length;i++){
                    String ph = ma[i];
                    Map<String, Object> map=new HashMap<>();
                    map.put("phone",ph);
                    map.put("venuesAddres",arr);
                    map.put("venuesName",nm);
                    map.put("event",event);
                    /*String sessionId = VoiceCall.voiceCall(map);
                    //保存数据
                    FeeInfo feeInfo =new FeeInfo();
                    feeInfo.setSessionId(sessionId);
                    feeInfo.setEventType(CallEnums.fee.getCode());
                    feeInfo.setRefEventId(String.valueOf(relEventId));
                    eventNotifiedMapper.addCall(feeInfo);*/
                }
            }
            //监管
            if(!GeneTool.isEmpty(users)){
                String[] us = users.split(",");
                for(int j=0;j<us.length;j++){
                    String ph = us[j];
                    Map<String, Object> umap=new HashMap<>();
                    umap.put("phone",ph);
                    umap.put("venuesAddres",arr);
                    umap.put("venuesName",nm);
                    umap.put("event",event);
                    /*String sessionId = VoiceCall.voiceCall(umap);
                    //保存数据
                    FeeInfo feeInfo =new FeeInfo();
                    feeInfo.setSessionId(sessionId);
                    feeInfo.setEventType(CallEnums.fee.getCode());
                    feeInfo.setRefEventId(String.valueOf(relEventId));
                    eventNotifiedMapper.addCall(feeInfo);*/
                }
            }
        }
    }

    @Override
    public void metricRecord() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        System.out.println(localDateTime);
        //获取预警事件记录总数
        int eventAdd = appmetricLogMapper.getEventAdd(localDateTime);
        if(eventAdd>0){
            AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"预警事件",String.valueOf(eventAdd),"A330000100000202105005924");
            appmetricLogMapper.add(metricLog);
        }
        //获取任务完成总数
        HashMap<String, BigDecimal> taskMap = appmetricLogMapper.getTask(localDateTime);
        if(null!=taskMap && taskMap.size()>0){
            BigDecimal report = taskMap.get("report");
            if(report.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"任务上报",String.valueOf(eventAdd),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
            BigDecimal issued = taskMap.get("issued");
            if(issued.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"任务下达",String.valueOf(issued),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
            BigDecimal filing = taskMap.get("filing");
            if(filing.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"活动备案",String.valueOf(filing),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
            BigDecimal upd = taskMap.get("upd");
            if(upd.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"场所更新",String.valueOf(upd),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
            BigDecimal warn = taskMap.get("warn");
            if(warn.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"预警处理",String.valueOf(warn),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
        }
        //获取短信通知
        String notify = appmetricLogMapper.getNotify(localDateTime);
        if(!StringUtils.isEmpty(notify)){
            int notyNum = notify.split(",").length;
            if(notyNum>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"短信通知",String.valueOf(notyNum),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
        }
    }
}
