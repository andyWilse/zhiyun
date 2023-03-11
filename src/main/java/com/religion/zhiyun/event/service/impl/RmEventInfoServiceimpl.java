package com.religion.zhiyun.event.service.impl;

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
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.sms.SendMassage;
import com.religion.zhiyun.venues.entity.ParamsVo;
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
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public OutInterfaceResponse addEvent(String eventJson) {
        System.out.println("AI告警:"+eventJson);
        //log.info("AI告警:"+eventJson);
        long code=0l;
        String message="";
        try{
            Map<String, Object> map = JsonUtils.jsonToMap(eventJson);
            String alarmName= (String) map.get("alarmName");//预警类型名称
            String alarmCode= (String) map.get("alarmCode");//预警类型编码
            String alarmLevelName= (String) map.get("alarmLevelName");//程度：（一般）
            String durationTime= (String) map.get("durationTime");

            String cameraId= (String) map.get("cameraId");//摄像id
            String cameraName= (String) map.get("cameraName");//摄像名称
            String videoUrl= (String) map.get("videoUrl");
            String timeStamp= (String) map.get("timeStamp");

            String picture= (String) map.get("picture");
            List<String> pictures= (List<String>) map.get("pictures");
            String pictureRec= (String) map.get("pictureRec");
            List<String> pictureRecs= (List<String>) map.get("pictureRecs");//图片地址

            String locationName= (String) map.get("locationName");//位置
            String streetName= (String) map.get("streetName");//街道
            String latLong= (String) map.get("latLong");//经纬度

            String infoSource= (String) map.get("infoSource");//信息来源（移动通信）
            String algoUuid= (String) map.get("algoUuid");
            String algoName= (String) map.get("algoName");

            String extType= (String) map.get("extType");
            String extData= (String) map.get("extData");
            Double id= (Double) map.get("id");

            int relVenuesId=10000004;//需要场所名称，获取场所信息

            //1.摄像设备处理
            if(null!=videoUrl && !videoUrl.isEmpty() && null!=cameraId && !cameraId.isEmpty()){
                //判断数据库是否存在
                MonitroEntity monitors = rmMonitroInfoMapper.getMonitorsList(cameraId);
                if(null==monitors){
                    //不存在新增
                    MonitroEntity mo=new MonitroEntity();
                    mo.setMonitorUrl(videoUrl);
                    mo.setAccessNumber(cameraId);
                    mo.setVenuesAddres(locationName);
                    mo.setRelVenuesId(relVenuesId);
                    mo.setState( ParamCode.MONITOR_STATE_01.getCode());//在线
                    mo.setFunctionType(infoSource);
                    mo.setCreator("AI告警");
                    mo.setCreateTime(timeStamp);
                    mo.setLastModifier("AI告警");
                    mo.setLastModifyTime(timeStamp);
                    rmMonitroInfoMapper.addMonitro(mo);
                }else{
                    //存在，更新
                    monitors.setMonitorUrl(videoUrl);
                    monitors.setVenuesAddres(locationName);
                    //需要场所名称，获取场所信息
                    monitors.setRelVenuesId(relVenuesId);
                    monitors.setState( ParamCode.MONITOR_STATE_01.getCode());//在线
                    monitors.setFunctionType(infoSource);
                    monitors.setLastModifier("AI告警");
                    monitors.setLastModifyTime(timeStamp);
                    rmMonitroInfoMapper.updateMonitro(monitors);
                }
            }

            //2.预警信息处理
            EventEntity event=new EventEntity();
            event.setAccessNumber(cameraId);
            event.setWarnTime(timeStamp);
            //预警类型
            if("TRASH_ACCUMULATION".equals(alarmCode)){
                event.setEventType("03");
            }else if("CROWDS_GATHER".equals(alarmCode)){//人群聚集
                event.setEventType("04");
            }else{
                event.setEventType("01");
            }
            //程度
            if("轻微".equals(alarmLevelName)){
                event.setEventLevel("1");
            }else if("一般".equals(alarmLevelName)){
                event.setEventLevel("2");
            }else{
                event.setEventLevel("3");
            }
            event.setEventState(ParamCode.EVENT_STATE_00.getCode());
            event.setRelVenuesId(relVenuesId);
            event.setHandleTesults(ParamCode.EVENT_STATE_00.getMessage());
            event.setHandleTime(timeStamp);
            //查询数据库数据是否存在，不存在新增；存在，修改
            EventEntity eventEntity = rmEventInfoMapper.queryEvent(event);
            int eventId =0;
            if(null!=eventEntity){
                //更新
                eventId = eventEntity.getEventId();
                //rmEventInfoMapper.updateEvent(eventEntity);
            }else{
                //新增
                eventId = rmEventInfoMapper.addEvent(event);
            }

            //3.新增通知
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
    public AppResponse getByType(Integer page, Integer size,String eventType,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="根据预警类型查询预警信息";
        List<Map<String, Object>> mapList=null;
        Long total=0L;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            if("all".equals(eventType)){
                eventType="";
            }
            ParamsVo auth = this.getAuth(token);
            auth.setSize(size);
            auth.setPage(page);
            auth.setSearchOne(eventType);
            mapList = rmEventInfoMapper.getByType(auth);
            total=rmEventInfoMapper.getTotal(auth);

            code=ResultCode.SUCCESS.getCode();
            message="预警事件台账，查询成功";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message="预警事件台账，查询失败";
            e.printStackTrace();
        }
        return new AppResponse(code,message,total,mapList.toArray());
    }

    @Override
    public List<Map<String, Object>> getAllNum() {
        return rmEventInfoMapper.getAllNum();
    }

    @Override
    public RespPageBean getEventsByPage(Integer page, Integer size, String accessNumber,String token) {
        List<EventEntity> dataList = new ArrayList<>();
        Long total=0l;
        long code= ResultCode.FAILED.getCode();
        String result="预警查询pc！";
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            ParamsVo auth = this.getAuth(token);
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(accessNumber);
            dataList=rmEventInfoMapper.getEventsByPage(auth);
            total=rmEventInfoMapper.getTotal(auth);

            code= ResultCode.SUCCESS.getCode();
            result="预警查询成功！";
        } catch (RuntimeException r) {
            result=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new RespPageBean(code,result,total,dataList.toArray());
    }

    @Override
    public RespPageBean getUndoEvents(Integer page, Integer size,String token) {
        long code= ResultCode.FAILED.getCode();
        String result="未完成预警查询失败！";
        List<Map<String, Object>> events = new ArrayList<>();
        Long undoEventsTotal=0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //参数封装
            ParamsVo auth = this.getAuth(token);
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(ParamCode.EVENT_STATE_00.getCode());
            auth.setSearchTwo("");

            events = rmEventInfoMapper.getUndoEvents(auth);
            for(int i=0;i<events.size();i++){
                Map<String, Object> map = events.get(0);
                String path = (String) map.get("path");
                String[] split = path.split(",");
                map.put("path",split[0]);
            }

            undoEventsTotal = rmEventInfoMapper.getUndoEventsTotal(auth);
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
    public PageResponse getUndoEventDetail(String eventId) {
        long code= ResultCode.FAILED.getCode();
        String result="未完成预警详情查询失败！";
        List<Map<String, Object>> eventDetail = new ArrayList<>();
        try {
            eventDetail = rmEventInfoMapper.getEventDetail(eventId);
            code=ResultCode.SUCCESS.getCode();
            result="未完成预警详情查询成功！";
        } catch (RuntimeException e) {
            result=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            result=e.getMessage();
            e.printStackTrace();
        }
        return new PageResponse(code,result,eventDetail.toArray());
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
    public AppResponse reportOne(String eventId,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="一键上报";
        try {
            String mobil = this.getLogin(token);
            TaskEntity taskEntity=new TaskEntity();
            EventEntity event = rmEventInfoMapper.getEventById(eventId);
            if(null!=event){
                taskEntity.setTaskType(event.getEventType());
                taskEntity.setEndTime(TimeTool.strToDate(event.getWarnTime()));
                taskEntity.setTaskName("预警事件");
                taskEntity.setTaskContent("预警事件,请处理");
                taskEntity.setRelVenuesId(String.valueOf(event.getRelVenuesId()));
                taskEntity.setEmergencyLevel("01");
            }

            /*if("10000006".equals(identify) || "10000007".equals(identify)){
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
            }*/
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

        String user="";
        String manager="";
        try {
            NotifiedEntity notifiedEntity=new NotifiedEntity();
            //根据场所获取场所三人驻堂的成员
            List<Map<String, Object>> userList = sysUserMapper.getSanByVenues(relVenuesId);

            if(null!=userList && userList.size()>0){
                for(int i=0;i<userList.size();i++){
                    Map<String, Object> map = userList.get(i);
                    for(String key:map.keySet()){
                        String userNbr = (String) map.get(key);
                        user=user+userNbr+",";
                    }
                }
                notifiedEntity.setNotifiedUser(user);
            }else{
                //throw new RuntimeException("无三人驻堂成员");
            }

            //火灾预警全员推送，火警信息默认推送给该场所相关的所有工作人员，（工作人员包含三人驻堂的成员，和该场所相关的教职人员）
            if(ParamCode.EVENT_TYPE_01.getCode().equals(eventType)){
                //根据场所获取场所相关的教职人员
                List<StaffEntity> staffByVenuesId = rmStaffInfoMapper.getStaffByVenuesId(relVenuesId);
                if(null!=staffByVenuesId && staffByVenuesId.size()>0){
                    for(int i=0;i<staffByVenuesId.size();i++){
                        StaffEntity staffEntity = staffByVenuesId.get(i);
                        manager=manager+staffEntity.getStaffCd()+",";
                    }
                    notifiedEntity.setNotifiedStaff(manager);
                }
            }
            //保存
            notifiedEntity.setRefEventId(relEventId);
            notifiedEntity.setNotifiedFlag(ParamCode.NOTIFIED_FLAG_01.getCode());
            notifiedEntity.setNotifiedTime(new Date());
            eventNotifiedMapper.addNotified(notifiedEntity);
            //普通预警仅上报才给领导推送
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("预警信息已通知：三人驻堂的成员（"+user+");其他人员（"+manager+")");
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

    /**
     * 获取登录人
     * @return
     */
    public String getLogin(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("登录过期，请重新登陆！");
        }
        return loginNm;
    }

    /**
     * 获取
     * @return
     */
    public ParamsVo getAuth(String token){
        String login = this.getLogin(token);
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
        String area="";
        String town ="";
        String relVenuesId="";
        String[] venuesArr={};
        if(null!=sysUserEntity){
            relVenuesId = sysUserEntity.getRelVenuesId();
            town = sysUserEntity.getTown();
            area = sysUserEntity.getArea();
        }else{
            throw new RuntimeException("用户已过期，请重新登录！");
        }
        ParamsVo vo=new ParamsVo();
        vo.setArea(area);
        vo.setTown(town);
        vo.setVenues(relVenuesId);
        if(null!=relVenuesId && !relVenuesId.isEmpty()){
            venuesArr=relVenuesId.split(",");
            vo.setVenuesArr(venuesArr);
        }
        return vo;

    }

}
