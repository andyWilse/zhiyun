package com.religion.zhiyun.event.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.religion.zhiyun.event.dao.EventNotifiedMapper;
import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.entity.NotifiedEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.OutInterfaceResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.sms.SendMassage;
import com.religion.zhiyun.utils.sms.SendVerifyCode;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class RmEventInfoServiceimpl implements RmEventInfoService {
    @Autowired
    private RmEventInfoMapper rmEventInfoMapper;

    @Autowired
    private RmMonitroInfoMapper rmMonitroInfoMapper;

    @Autowired
    private RmStaffInfoMapper rmStaffInfoMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private EventNotifiedMapper eventNotifiedMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    TaskInfoMapper taskInfoMapper;

    @Override
    @Transactional
    public OutInterfaceResponse addEvent(String eventJson) {
        System.out.println("AI告警:"+eventJson);
        //log.info("AI告警:"+eventJson);
        long code=0l;
        String message="";
        try{
            Map<String, Object> map = JsonUtils.jsonToMap(eventJson);
            String videoUrl= (String) map.get("videoUrl");
            String cameraId= (String) map.get("cameraId");
            String locationName= (String) map.get("locationName");
            String timeStamp= (String) map.get("timeStamp");

            String alarmCode= (String) map.get("alarmCode");
            String alarmLevelName= (String) map.get("alarmLevelName");
            String infoSource= (String) map.get("infoSource");

            //新增设备
            if(!videoUrl.isEmpty() && !cameraId.isEmpty()){
                MonitroEntity moni=new MonitroEntity();
                moni.setMonitorUrl(videoUrl);
                moni.setAccessNumber(cameraId);
                moni.setVenuesAddres(locationName);
                moni.setCreator("AI告警");
                moni.setCreateTime(timeStamp);
                moni.setLastModifier("AI告警");
                moni.setLastModifyTime(timeStamp);
                moni.setRelVenuesId(10010);
                moni.setState("01");
                moni.setFunctionType(infoSource);
                rmMonitroInfoMapper.addMonitro(moni);
            }

            //新增预警
            EventEntity event=new EventEntity();
            event.setAccessNumber(cameraId);
            event.setWarnTime(timeStamp);
            if(alarmCode.equals("TRASH_ACCUMULATION")){
                event.setEventType("02");
            }else{
                event.setEventType("04");
            }
            event.setEventState("00");
            event.setRelVenuesId(10010);
            event.setHandleTesults(alarmLevelName);
            event.setHandleTime(timeStamp);
            //查询数据库数据是否存在，不存在新增；存在，修改
            List<Map<String,Object>> list = rmEventInfoMapper.queryEvent(event);
            int eventId =0;
            if(list.size()>0){
                //更新
                eventId = (int) list.get(0).get("id");
                rmEventInfoMapper.updateEvent(event);
            }else{
                //新增
                //String notifiedParty = this.getNotifiedParty(event.getEventType(), event.getRelVenuesId());
                //event.setResponsiblePerson(notifiedParty);
                eventId = rmEventInfoMapper.addEvent(event);
            }
            //新增通知
            this.addNotifiedParty(event.getEventType(), event.getRelVenuesId(),eventId);

            code=ResultCode.SUCCESS.getCode();
            message="AI告警,数据处理成功！";
        }catch (Exception e){
            code=ResultCode.FAILED.getCode();
            message="AI告警,数据处理失败！";
            e.printStackTrace();
        }

        return new OutInterfaceResponse(code,message);

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
    public void deleteEvent(int eventId) {
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
    public AppResponse getByType(Integer page, Integer size,String eventType) {
        long code= ResultCode.SUCCESS.getCode();
        String message="";
        AppResponse bean = new AppResponse();
        List<Map<String, Object>> mapList=null;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            if("all".equals(eventType)){
                eventType="";
            }
            mapList = rmEventInfoMapper.getByType(page,size,eventType);
            Object[] objects = mapList.toArray();
            Long total=rmEventInfoMapper.getTotal();
            bean.setResult(objects);
            bean.setTotal(total);
            message="预警事件台账，查询成功";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="预警事件台账，查询失败";
            e.printStackTrace();
        }
        bean.setCode(code);
        bean.setMessage(message);
        return bean;
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

    @Override
    public RespPageBean getUndoEvents(Integer page, Integer size) {
        long code= ResultCode.FAILED.getCode();
        String result="未完成预警查询失败！";
        List<Map<String, Object>> events = null;
        Long undoEventsTotal=0l;
        try {
            events = rmEventInfoMapper.getUndoEvents(page,size,ParamCode.EVENT_STATE_00.getCode(),"");
            for(int i=0;i<events.size();i++){
                Map<String, Object> map = events.get(0);
                String path = (String) map.get("path");
                String[] split = path.split(",");
                map.put("path",split[0]);
            }

            undoEventsTotal = rmEventInfoMapper.getUndoEventsTotal(ParamCode.EVENT_STATE_00.getCode(), "");
            code=ResultCode.SUCCESS.getCode();
            result="未完成预警查询成功！";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            result="未完成预警查询失败！";
            e.printStackTrace();
        }

        return new RespPageBean(code,result,undoEventsTotal,events);
    }

    @Override
    public OutInterfaceResponse addEventByNB(String eventEntity) {
        System.out.println("接受NB烟感器的数据:"+eventEntity);
        //log.info("接受NB烟感器的数据:"+eventEntity);
        long code=0l;
        String message="";
        String eventInfo="";
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(eventEntity);
            EventEntity event = new EventEntity();
            String deviceType = (String) map.get("deviceType");//设备类型
            String deviceId = (String) map.get("deviceId");
            String deviceName = (String) map.get("deviceName");//设备名字
            String at = (String) map.get("at");//事发时间
            String type = (String) map.get("type");//事件类型   "alarm": 告警；"clean": 告警清除；"reset": 设备重置；"dat": 数据上报
            String data = (String) map.get("data");//消息内容
            double level = (double) map.get("level");//消息  事件=0,次要=1,重要=2,严重=3, 不填为严重。（火警默认严重
            int intValue = (int) level;
            String location = (String) map.get("location");//位置
            String rawData = (String) map.get("rawData");//原始数据

            event.setDeviceName(deviceName);
            event.setAccessNumber(deviceId);
            event.setDeviceType(deviceType);
            event.setWarnTime(at);
            event.setEventType("01");//默认传来的都是火警
            event.setRawData(rawData);
            event.setEventData(data);
            event.setEventLevel(String.valueOf(intValue));
            event.setLocation(location);
            event.setEventState("00");
            event.setHandleTesults("待处理");
            event.setRelVenuesId(10000001);

            Timestamp timestamp = new Timestamp(new Date().getTime());
            String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            event.setHandleTime(format);
            rmEventInfoMapper.addEventByNB(event);
            String relVenuesId = String.valueOf(event.getRelVenuesId());

            //发送短信通知
            String location1 = event.getLocation();//地址
            eventInfo = this.sendRemindInfo(relVenuesId,location1);
            code=ResultCode.SUCCESS.getCode();
            message="NB烟感器数据处理成功！";
        }catch (Exception e){
            code=ResultCode.FAILED.getCode();
            message="NB烟感器数据处理失败！";
            e.printStackTrace();
        }

        return new OutInterfaceResponse(code,message,eventInfo);
    }

    //根据事件场所查询关联人的电话
    public  String sendRemindInfo(String venuesId,String location1){
        //根据场所id查询关联人
        List<SysUserEntity> mobile = rmEventInfoMapper.getMobile(venuesId);
        String s="";
        String contents="【云监控中心】您好！位于"+location1+"疑似发生火灾，请您立刻前去处理！！";
        for (int i=0;i<mobile.size();i++){
            //获取关联人电话
            String userMobile = mobile.get(i).getUserMobile();
            //System.out.println(userMobile);
            //发送短信
            s = SendMassage.sendSms(contents, userMobile);
            System.out.println("已发送"+(i+1)+"条短信");

        }
        return s;
    }


    @Override
    public AppResponse getEventsByState(Integer page, Integer size, String eventState) {
        String login="ab1";
        long code= ResultCode.SUCCESS.getCode();
        String message="";
        AppResponse bean = new AppResponse();
        List<Map<String, Object>> mapList=null;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            String[] arr =null;
            if("01".equals(eventState)){
                arr= new String[]{"01", "02"};
            }else{
                arr= new String[]{eventState};
            }
            mapList = rmEventInfoMapper.getEventsByState(page,size,arr,login);
            Object[] objects = mapList.toArray();
            Long total=rmEventInfoMapper.getTotalByState(arr,login);
            bean.setResult(objects);
            bean.setTotal(total);
            message="预警通知，查询成功";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="预警通知，查询失败";
            e.printStackTrace();
        }
        bean.setCode(code);
        bean.setMessage(message);
        return bean;
    }

    @Override
    @Transactional
    public AppResponse dismissEvent(String eventId) {
        long code= ResultCode.SUCCESS.getCode();
        String message="";
        try {
            //更新预警事件表
            rmEventInfoMapper.updateEventState(eventId,new Date(),ParamCode.EVENT_STATE_04.getCode());
            //更新通知
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_04.getCode());
            message="误报解除成功";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="误报解除失败";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse callFire(String eventId) {
        long code= ResultCode.SUCCESS.getCode();
        String message="";
        try {
            //更新预警事件表
            rmEventInfoMapper.updateEventState(eventId,new Date(),ParamCode.EVENT_STATE_03.getCode());
            //更新通知
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_03.getCode());
            message="拨打119成功";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="拨打119失败";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse reportOne(String eventId) {
        long code= ResultCode.SUCCESS.getCode();
        String message="";
        try {
            String login="zuyuan2";
            String identify="10000007";
            TaskEntity taskEntity=new TaskEntity();
            EventEntity event = rmEventInfoMapper.getEventById(eventId);
            if(null!=event){
                taskEntity.setTaskType(event.getEventType());
                taskEntity.setEndTime(TimeTool.strToDate(event.getWarnTime()));
                taskEntity.setTaskName("预警事件");
                taskEntity.setTaskContent("预警事件,请处理");
                taskEntity.setRelVenuesId(event.getRelVenuesId()+"");
                taskEntity.setEmergencyLevel("01");
            }

            if("10000006".equals(identify) || "10000007".equals(identify)){
                //查找街干事、街委员
                List<SysUserEntity> jie = sysUserMapper.getJie(login, identify);
                if(jie.size()>0 && null!=jie){
                    for(int i=0;i<jie.size();i++){
                        this.launch(taskEntity,jie.get(i).getLoginNm());
                    }
                }
            }else if("10000004".equals(identify) || "10000005".equals(identify)){
                List<SysUserEntity> qu = sysUserMapper.getQu(login, identify);
                if(qu.size()>0 && null!=qu){
                    for(int i=0;i<qu.size();i++){
                        this.launch(taskEntity,qu.get(i).getLoginNm());
                    }
                }
            }
            //根据用户身份，查询
            //发起流程
            //根据用户查询上级
            String tel="";
            //更新预警事件表
            rmEventInfoMapper.updateEventState(eventId,new Date(),ParamCode.EVENT_STATE_02.getCode());
            //更新通知
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_02.getCode());
            message="一键上报成功";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="一键上报失败";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    public AppResponse getEventsMonth(int num,String type) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        List<Map<String, Object>> eventsMonth=null;
        try {
            eventsMonth = rmEventInfoMapper.getEventsMonth(num,type);
            code= ResultCode.SUCCESS.getCode();
            message="月统计事件成功！";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="月统计事件失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsMonth.toArray());
    }

    @Override
    public AppResponse getEventsDay(int num,String type) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        List<Map<String, Object>> eventsDay=null;
        try {
            eventsDay = rmEventInfoMapper.getEventsDay(num,type);
            code= ResultCode.SUCCESS.getCode();
            message="天统计事件成功！";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="天统计事件失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsDay.toArray());
    }

    @Override
    public AppResponse getEventsWeek(int num, int dayNum,String type) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        List<Map<String, Object>> eventsWeek=null;
        try {
            eventsWeek = rmEventInfoMapper.getEventsWeek(num, dayNum,type);
            code= ResultCode.SUCCESS.getCode();
            message="周统计事件成功！";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="周统计事件失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsWeek.toArray());
    }

    @Override
    public AppResponse getEventsGather(int num,String dateType) {
        long code= ResultCode.FAILED.getCode();
        String message="统计事件汇总获取失败！";
        List<Map<String, Object>> eventsGather=null;
        try {
            if("month".equals(dateType)){
                eventsGather = rmEventInfoMapper.getEventsMonthGather(num);
            }else if("week".equals(dateType)){
                //int dayNum=7*(num+1)-1;
                eventsGather = rmEventInfoMapper.getEventsWeekGather(num);
            }else if("day".equals(dateType)){
                eventsGather = rmEventInfoMapper.getEventsDayGather(num);
            }
            code= ResultCode.SUCCESS.getCode();
            message="统计事件汇总获取成功！";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="统计事件汇总获取失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsGather.toArray());
    }


    /**
     * 预警通知保存
     * @param eventType
     * @param relVenuesId
     * @param relEventId
     * @return
     */
    public void addNotifiedParty(String eventType,int relVenuesId,int relEventId) {
        SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-DD HH:SS:MM");
        String tm = format.format(new Date());
        String person="";
        //根据场所获取场所三人驻堂的成员
        List<Map<String, Object>> userByVenuesId = sysUserMapper.getSanByVenues(relVenuesId);
        if(null!=userByVenuesId && userByVenuesId.size()>0){
            Map<String, Object> map = userByVenuesId.get(0);
            for(String key:map.keySet()){
                String o = (String) map.get(key);
                person=o+",";
                NotifiedEntity notifiedEntity=new NotifiedEntity();
                notifiedEntity.setRefEventId(relEventId);
                notifiedEntity.setNotifiedFlag("1");
                notifiedEntity.setNotifiedTime(tm);
                notifiedEntity.setNotifiedUser(o);
                eventNotifiedMapper.addNotified(notifiedEntity);
            }
        }
        //火灾预警全员推送，火警信息默认推送给该场所相关的所有工作人员，（工作人员包含三人驻堂的成员，和该场所相关的教职人员）
        if(ParamCode.EVENT_TYPE_01.equals(eventType)){
            //根据场所获取场所相关的教职人员
            List<StaffEntity> staffByVenuesId = rmStaffInfoMapper.getStaffByVenuesId(relVenuesId);
            if(null!=staffByVenuesId && staffByVenuesId.size()>0){
                for(int i=0;i<staffByVenuesId.size();i++){
                    StaffEntity staffEntity = staffByVenuesId.get(i);
                    person=staffEntity.getStaffCd()+",";
                    NotifiedEntity notifiedEntity=new NotifiedEntity();
                    notifiedEntity.setRefEventId(relEventId);
                    notifiedEntity.setNotifiedFlag("01");
                    notifiedEntity.setNotifiedTime(tm);
                    notifiedEntity.setNotifiedStaff(staffEntity.getStaffCd());
                    eventNotifiedMapper.addNotified(notifiedEntity);
                }
            }
        }
        //普通预警仅上报才给领导推送
        else{
        }

        log.info("预警信息已通知："+person);
    }

    @Transactional
    public Object launch(TaskEntity taskEntity,String next) {
        String procInstId="";
        try {
            //String loginNm = this.getLogin();
            String loginNm ="ab";
            Authentication.setAuthenticatedUserId(loginNm);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
            //inputUser就是在bpmn中Assignee配置的参数
            Map<String, Object> variables = new HashMap<>();
            variables.put("assignee2", next);
            /**start**/
            //开启流程。myProcess_2为流程名称。获取方式把bpmn改为xml文件就可以看到流程名
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
            ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode(),variables);
            String processInstanceId = processInstance.getProcessInstanceId();
            /**end**/
            //完成此节点。由下一节点审批。完成后act_ru_task会创建一条由下节点审批的数据
            TaskQuery taskQuery = taskService.createTaskQuery();
            Task tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
            procInstId=tmp.getProcessInstanceId();
            //taskService.setAssignee("assignee2",userNbr);
            taskService.complete(tmp.getId(),variables);

            taskEntity.setLaunchPerson(loginNm);
            taskEntity.setLaunchTime(new Date());
            taskEntity.setTaskType(TaskParamsEnum.ZY_REPORT_TASK_KEY.getName());
            taskEntity.setProcInstId(procInstId);
            taskEntity.setFlowType("01");
            //保存任务信息
            taskInfoMapper.addTask(taskEntity);

            log.info("任务id："+processInstanceId+" 发起申请，任务开始！");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("未知错误，请联系管理员！") ;
        }

        return "流程id(唯一标识)procInstId:"+procInstId;
    }



}
