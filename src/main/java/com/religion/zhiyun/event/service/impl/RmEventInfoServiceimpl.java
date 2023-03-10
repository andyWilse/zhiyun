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
        System.out.println("AI??????:"+eventJson);
        //log.info("AI??????:"+eventJson);
        long code=0l;
        String message="";
        try{
            Map<String, Object> map = JsonUtils.jsonToMap(eventJson);
            String alarmName= (String) map.get("alarmName");//??????????????????
            String alarmCode= (String) map.get("alarmCode");//??????????????????
            String alarmLevelName= (String) map.get("alarmLevelName");//?????????????????????
            String durationTime= (String) map.get("durationTime");

            String cameraId= (String) map.get("cameraId");//??????id
            String cameraName= (String) map.get("cameraName");//????????????
            String videoUrl= (String) map.get("videoUrl");
            String timeStamp= (String) map.get("timeStamp");

            String picture= (String) map.get("picture");
            List<String> pictures= (List<String>) map.get("pictures");
            String pictureRec= (String) map.get("pictureRec");
            List<String> pictureRecs= (List<String>) map.get("pictureRecs");//????????????

            String locationName= (String) map.get("locationName");//??????
            String streetName= (String) map.get("streetName");//??????
            String latLong= (String) map.get("latLong");//?????????

            String infoSource= (String) map.get("infoSource");//??????????????????????????????
            String algoUuid= (String) map.get("algoUuid");
            String algoName= (String) map.get("algoName");

            String extType= (String) map.get("extType");
            String extData= (String) map.get("extData");
            Double id= (Double) map.get("id");

            int relVenuesId=10000004;//???????????????????????????????????????

            //1.??????????????????
            if(null!=videoUrl && !videoUrl.isEmpty() && null!=cameraId && !cameraId.isEmpty()){
                //???????????????????????????
                MonitroEntity monitors = rmMonitroInfoMapper.getMonitorsList(cameraId);
                if(null==monitors){
                    //???????????????
                    MonitroEntity mo=new MonitroEntity();
                    mo.setMonitorUrl(videoUrl);
                    mo.setAccessNumber(cameraId);
                    mo.setVenuesAddres(locationName);
                    mo.setRelVenuesId(relVenuesId);
                    mo.setState( ParamCode.MONITOR_STATE_01.getCode());//??????
                    mo.setFunctionType(infoSource);
                    mo.setCreator("AI??????");
                    mo.setCreateTime(timeStamp);
                    mo.setLastModifier("AI??????");
                    mo.setLastModifyTime(timeStamp);
                    rmMonitroInfoMapper.addMonitro(mo);
                }else{
                    //???????????????
                    monitors.setMonitorUrl(videoUrl);
                    monitors.setVenuesAddres(locationName);
                    //???????????????????????????????????????
                    monitors.setRelVenuesId(relVenuesId);
                    monitors.setState( ParamCode.MONITOR_STATE_01.getCode());//??????
                    monitors.setFunctionType(infoSource);
                    monitors.setLastModifier("AI??????");
                    monitors.setLastModifyTime(timeStamp);
                    rmMonitroInfoMapper.updateMonitro(monitors);
                }
            }

            //2.??????????????????
            EventEntity event=new EventEntity();
            event.setAccessNumber(cameraId);
            event.setWarnTime(timeStamp);
            //????????????
            if("TRASH_ACCUMULATION".equals(alarmCode)){
                event.setEventType("03");
            }else if("CROWDS_GATHER".equals(alarmCode)){//????????????
                event.setEventType("04");
            }else{
                event.setEventType("01");
            }
            //??????
            if("??????".equals(alarmLevelName)){
                event.setEventLevel("1");
            }else if("??????".equals(alarmLevelName)){
                event.setEventLevel("2");
            }else{
                event.setEventLevel("3");
            }
            event.setEventState(ParamCode.EVENT_STATE_00.getCode());
            event.setRelVenuesId(relVenuesId);
            event.setHandleTesults(ParamCode.EVENT_STATE_00.getMessage());
            event.setHandleTime(timeStamp);
            //?????????????????????????????????????????????????????????????????????
            EventEntity eventEntity = rmEventInfoMapper.queryEvent(event);
            int eventId =0;
            if(null!=eventEntity){
                //??????
                eventId = eventEntity.getEventId();
                //rmEventInfoMapper.updateEvent(eventEntity);
            }else{
                //??????
                eventId = rmEventInfoMapper.addEvent(event);
            }

            //3.????????????
            this.addNotifiedParty(event.getEventType(), event.getRelVenuesId(),eventId);

            code=ResultCode.SUCCESS.getCode();
            message="AI??????,?????????????????????";
        }catch (Exception e){
            code=ResultCode.FAILED.getCode();
            message="AI??????,?????????????????????";
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
        String message="????????????????????????????????????";
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
            message="?????????????????????????????????";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message="?????????????????????????????????";
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
        String result="????????????pc???";
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
            result="?????????????????????";
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
        String result="??????????????????????????????";
        List<Map<String, Object>> events = new ArrayList<>();
        Long undoEventsTotal=0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //????????????
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
            result="??????????????????????????????";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            result="??????????????????????????????";
            e.printStackTrace();
        }

        return new RespPageBean(code,result,undoEventsTotal,events);
    }

    @Override
    public PageResponse getUndoEventDetail(String eventId) {
        long code= ResultCode.FAILED.getCode();
        String result="????????????????????????????????????";
        List<Map<String, Object>> eventDetail = new ArrayList<>();
        try {
            eventDetail = rmEventInfoMapper.getEventDetail(eventId);
            code=ResultCode.SUCCESS.getCode();
            result="????????????????????????????????????";
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
        System.out.println("??????NB??????????????????:"+eventEntity);
        //log.info("??????NB??????????????????:"+eventEntity);
        long code=0l;
        String message="";
        String eventInfo="";
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(eventEntity);
            EventEntity event = new EventEntity();
            String deviceType = (String) map.get("deviceType");//????????????
            String deviceId = (String) map.get("deviceId");
            String deviceName = (String) map.get("deviceName");//????????????
            String at = (String) map.get("at");//????????????
            String type = (String) map.get("type");//????????????   "alarm": ?????????"clean": ???????????????"reset": ???????????????"dat": ????????????
            String data = (String) map.get("data");//????????????
            double level = (double) map.get("level");//??????  ??????=0,??????=1,??????=2,??????=3, ???????????????????????????????????????
            int intValue = (int) level;
            String location = (String) map.get("location");//??????
            String rawData = (String) map.get("rawData");//????????????

            event.setDeviceName(deviceName);
            event.setAccessNumber(deviceId);
            event.setDeviceType(deviceType);
            event.setWarnTime(at);
            event.setEventType("01");//???????????????????????????
            event.setRawData(rawData);
            event.setEventData(data);
            event.setEventLevel(String.valueOf(intValue));
            event.setLocation(location);
            event.setEventState("00");
            event.setHandleTesults("?????????");
            event.setRelVenuesId(10000001);

            Timestamp timestamp = new Timestamp(new Date().getTime());
            String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            event.setHandleTime(format);
            rmEventInfoMapper.addEventByNB(event);
            String relVenuesId = String.valueOf(event.getRelVenuesId());

            //??????????????????
            String location1 = event.getLocation();//??????
            eventInfo = this.sendRemindInfo(relVenuesId,location1);
            code=ResultCode.SUCCESS.getCode();
            message="NB??????????????????????????????";
        }catch (Exception e){
            code=ResultCode.FAILED.getCode();
            message="NB??????????????????????????????";
            e.printStackTrace();
        }

        return new OutInterfaceResponse(code,message,eventInfo);
    }

    //??????????????????????????????????????????
    public  String sendRemindInfo(String venuesId,String location1){
        //????????????id???????????????
        List<SysUserEntity> mobile = rmEventInfoMapper.getMobile(venuesId);
        String s="";
        String contents="????????????????????????????????????"+location1+"???????????????????????????????????????????????????";
        for (int i=0;i<mobile.size();i++){
            //?????????????????????
            String userMobile = mobile.get(i).getUserMobile();
            //System.out.println(userMobile);
            //????????????
            s = SendMassage.sendSms(contents, userMobile);
            System.out.println("?????????"+(i+1)+"?????????");

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
            message="???????????????????????????";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="???????????????????????????";
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
            //?????????????????????
            rmEventInfoMapper.updateEventState(eventId,new Date(),ParamCode.EVENT_STATE_04.getCode());
            //????????????
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_04.getCode());
            message="??????????????????";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="??????????????????";
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
            //?????????????????????
            rmEventInfoMapper.updateEventState(eventId,new Date(),ParamCode.EVENT_STATE_03.getCode());
            //????????????
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_03.getCode());
            message="??????119??????";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="??????119??????";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse reportOne(String eventId,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="????????????";
        try {
            String mobil = this.getLogin(token);
            TaskEntity taskEntity=new TaskEntity();
            EventEntity event = rmEventInfoMapper.getEventById(eventId);
            if(null!=event){
                taskEntity.setTaskType(event.getEventType());
                taskEntity.setEndTime(TimeTool.strToDate(event.getWarnTime()));
                taskEntity.setTaskName("????????????");
                taskEntity.setTaskContent("????????????,?????????");
                taskEntity.setRelVenuesId(String.valueOf(event.getRelVenuesId()));
                taskEntity.setEmergencyLevel("01");
            }

            /*if("10000006".equals(identify) || "10000007".equals(identify)){
                //???????????????????????????
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
            //???????????????????????????
            //????????????
            //????????????????????????
            String tel="";
            //?????????????????????
            rmEventInfoMapper.updateEventState(eventId,new Date(),ParamCode.EVENT_STATE_02.getCode());
            //????????????
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_02.getCode());
            message="??????????????????";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="??????????????????";
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
            message="????????????????????????";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="????????????????????????";
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
            message="????????????????????????";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="????????????????????????";
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
            message="????????????????????????";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="????????????????????????";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsWeek.toArray());
    }

    @Override
    public AppResponse getEventsGather(int num,String dateType) {
        long code= ResultCode.FAILED.getCode();
        String message="?????????????????????????????????";
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
            message="?????????????????????????????????";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="?????????????????????????????????";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsGather.toArray());
    }


    /**
     * ??????????????????
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
            //?????????????????????????????????????????????
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
                //throw new RuntimeException("?????????????????????");
            }

            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if(ParamCode.EVENT_TYPE_01.getCode().equals(eventType)){
                //?????????????????????????????????????????????
                List<StaffEntity> staffByVenuesId = rmStaffInfoMapper.getStaffByVenuesId(relVenuesId);
                if(null!=staffByVenuesId && staffByVenuesId.size()>0){
                    for(int i=0;i<staffByVenuesId.size();i++){
                        StaffEntity staffEntity = staffByVenuesId.get(i);
                        manager=manager+staffEntity.getStaffCd()+",";
                    }
                    notifiedEntity.setNotifiedStaff(manager);
                }
            }
            //??????
            notifiedEntity.setRefEventId(relEventId);
            notifiedEntity.setNotifiedFlag(ParamCode.NOTIFIED_FLAG_01.getCode());
            notifiedEntity.setNotifiedTime(new Date());
            eventNotifiedMapper.addNotified(notifiedEntity);
            //???????????????????????????????????????
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("????????????????????????????????????????????????"+user+");???????????????"+manager+")");
    }

    @Transactional
    public Object launch(TaskEntity taskEntity,String next) {
        String procInstId="";
        try {
            //String loginNm = this.getLogin();
            String loginNm ="ab";
            Authentication.setAuthenticatedUserId(loginNm);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
            //inputUser?????????bpmn???Assignee???????????????
            Map<String, Object> variables = new HashMap<>();
            variables.put("assignee2", next);
            /**start**/
            //???????????????myProcess_2?????????????????????????????????bpmn??????xml??????????????????????????????
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
            ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode(),variables);
            String processInstanceId = processInstance.getProcessInstanceId();
            /**end**/
            //???????????????????????????????????????????????????act_ru_task??????????????????????????????????????????
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
            //??????????????????
            taskInfoMapper.addTask(taskEntity);

            log.info("??????id???"+processInstanceId+" ??????????????????????????????");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("????????????????????????????????????") ;
        }

        return "??????id(????????????)procInstId:"+procInstId;
    }

    /**
     * ???????????????
     * @return
     */
    public String getLogin(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("?????????????????????????????????");
        }
        return loginNm;
    }

    /**
     * ??????
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
            throw new RuntimeException("????????????????????????????????????");
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
