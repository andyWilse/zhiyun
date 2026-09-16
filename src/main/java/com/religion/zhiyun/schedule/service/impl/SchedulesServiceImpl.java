package com.religion.zhiyun.schedule.service.impl;

import com.religion.zhiyun.event.dao.EventNotifiedMapper;
import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.schedule.service.SchedulesService;
import com.religion.zhiyun.sys.log.dao.AppmetricLogMapper;
import com.religion.zhiyun.sys.log.entity.AppmetricLogEntity;
import com.religion.zhiyun.task.dao.TaskActInstMapper;
import com.religion.zhiyun.task.entity.ActInstEntity;
import com.religion.zhiyun.task.service.impl.TaskAiWarnServiceImpl;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.enums.TaskActEnums;
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
    @Autowired
    private TaskActInstMapper taskActInstMapper;
    @Autowired
    private TaskAiWarnServiceImpl taskAiWarnServiceImpl;


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

    @Override
    public void repeatMessage() {
        //1.初审岗：在15分钟内未处理的预警事件，再次通知
        List<ActInstEntity> unReviewTask = taskActInstMapper.getUnAiTaskAct(15, TaskActEnums.AI_WARN_NODE_02.getCode());
        if(null!=unReviewTask && unReviewTask.size()>0){
            for(int i=0;i<unReviewTask.size();i++){
                ActInstEntity actInstEntity = unReviewTask.get(i);
                String actInstId = actInstEntity.getActInstId();
                String actReceiver = actInstEntity.getActReceiver();
                List<String> userSend = JsonUtils.jsonTOList(actReceiver, String.class);
                //短信通知
                taskAiWarnServiceImpl.sendMsg(actInstId,
                        userSend,
                        null,
                        null,
                        null,
                        null);
            }
        }
        //2.如果30分钟内未处理
        List<ActInstEntity> thirtyTask = taskActInstMapper.getUnAiTaskAct(30, null);
        if(null!=thirtyTask && thirtyTask.size()>0){
            for(int j=0;j<thirtyTask.size();j++){
                ActInstEntity thirtyEntity = thirtyTask.get(j);
                String actInstId = thirtyEntity.getActInstId();
                String actReceiver = thirtyEntity.getActReceiver();
                int actCode = thirtyEntity.getActCode();
                String acCode = String.valueOf(actCode);
                //2.1.初审岗：直接进入下一岗
                if(TaskActEnums.AI_WARN_NODE_02.getCode().equals(acCode)){
                    //继续流程
                    List<String> userSend = JsonUtils.jsonTOList(actReceiver, String.class);
                    taskAiWarnServiceImpl.review("初审通过",actInstId,"",userSend.get(0));
                }else {
                    //2.2.其他岗位：再次通知
                    //短信通知
                    List<String> userSend = JsonUtils.jsonTOList(actReceiver, String.class);
                    taskAiWarnServiceImpl.sendMsg(actInstId,
                            userSend,
                            null,
                            null,
                            null,
                            null);
                }
            }
        }

        //3.如果60分钟内未处理
        List<ActInstEntity> sixtyTask = taskActInstMapper.getUnAiTaskAct(60, null);
        if(null!=sixtyTask && sixtyTask.size()>0){
            for(int j=0;j<sixtyTask.size();j++){
                ActInstEntity sixtyEntity = sixtyTask.get(j);
                String actInstId = sixtyEntity.getActInstId();
                String actReceiver = sixtyEntity.getActReceiver();
                List<String> userSend = JsonUtils.jsonTOList(actReceiver, String.class);
                int actCode = sixtyEntity.getActCode();
                String acCode = String.valueOf(actCode);
                //继续流程
                if(TaskActEnums.AI_WARN_NODE_03.getCode().equals(acCode)
                    || TaskActEnums.AI_WARN_NODE_06.getCode().equals(acCode)){
                    //处置岗
                    taskAiWarnServiceImpl.handle(actInstId,"1","系统自动处理","","",userSend.get(0));
                }else if(TaskActEnums.AI_WARN_NODE_04.getCode().equals(acCode)){
                    //评审岗
                    taskAiWarnServiceImpl.evaluate(actInstId,"系统自动处理","",userSend.get(0));
                }
            }
        }

    }
}
