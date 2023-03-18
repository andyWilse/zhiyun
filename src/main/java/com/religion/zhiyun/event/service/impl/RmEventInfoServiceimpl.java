package com.religion.zhiyun.event.service.impl;

import com.religion.zhiyun.event.dao.EventNotifiedMapper;
import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.entity.NotifiedEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
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
import com.religion.zhiyun.venues.entity.ParamsVo;
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
    private RmFileMapper rmFileMapper;

    @Autowired
    TaskInfoMapper taskInfoMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public OutInterfaceResponse addEvent(String eventJson) {
        System.out.println("AI告警:"+eventJson);
        //log.info("AI告警:"+eventJson);
        long code=ResultCode.SUCCESS.getCode();
        String message="AI告警,数据处理！";

        EventEntity event=new EventEntity();
        int relVenuesId=10000004;//需要场所名称，获取场所信息
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
            //获取场所
            String venuesId= (String) map.get("venuesId");
            if(null==venuesId || venuesId.isEmpty()){

            }else{
                relVenuesId = Integer.parseInt(venuesId);
            }

            //1.摄像设备处理
            if(null!=pictureRecs && !pictureRecs.isEmpty()){
                event.setEventResource(ParamCode.EVENT_FILE_00.getCode());
            }else if(null!=videoUrl && !videoUrl.isEmpty() && null!=cameraId && !cameraId.isEmpty()){
                event.setEventResource(ParamCode.EVENT_FILE_01.getCode());
                //判断数据库是否存在
                List<MonitroEntity> monitorsList = rmMonitroInfoMapper.getMonitorsList(cameraId);
                if(null==monitorsList || monitorsList.size()<1){
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
                   /* monitorsList.get(0)
                    monitors.setMonitorUrl(videoUrl);
                    monitors.setVenuesAddres(locationName);
                    //需要场所名称，获取场所信息
                    monitors.setRelVenuesId(relVenuesId);
                    monitors.setState( ParamCode.MONITOR_STATE_01.getCode());//在线
                    monitors.setFunctionType(infoSource);
                    monitors.setLastModifier("AI告警");
                    monitors.setLastModifyTime(timeStamp);
                    rmMonitroInfoMapper.updateMonitro(monitors);*/
                }
            }

            //2.预警信息处理
            event.setAccessNumber(cameraId);
            event.setWarnTime(timeStamp);
            //预警类型
            if("TRASH_ACCUMULATION".equals(alarmCode)){
                event.setEventType(ParamCode.EVENT_TYPE_02.getCode());
            }else if("CROWDS_GATHER".equals(alarmCode)){//人群聚集
                event.setEventType(ParamCode.EVENT_TYPE_04.getCode());
            }else{
                event.setEventType(ParamCode.EVENT_TYPE_03.getCode());
            }
            //程度
            if("轻微".equals(alarmLevelName)){
                event.setEventLevel("1");
            }else if("一般".equals(alarmLevelName)){
                event.setEventLevel("2");
            }else{
                event.setEventLevel("0");
            }
            event.setEventState(ParamCode.EVENT_STATE_00.getCode());
            event.setRelVenuesId(relVenuesId);
            event.setHandleResults("0");
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
                rmEventInfoMapper.addEvent(event);
                eventId = event.getEventId();
            }

            //3.短信通知，新增通知
            this.addNotifiedParty(event.getEventType(), relVenuesId,eventId,locationName);

            code=ResultCode.SUCCESS.getCode();
            message="AI告警,数据处理成功！";
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e){
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
    public EventEntity getByEventId(String eventId) {
        return rmEventInfoMapper.getByEventId(eventId);
    }

    @Override
    public AppResponse getByType(Map<String, Object> map,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="根据预警类型查询预警信息";
        List<Map<String, Object>> mapList=null;
        Long total=0L;
        try {
            ParamsVo auth = this.getAuth(token);
            //分页
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            auth.setSize(size);
            auth.setPage(page);
            //预警类型（all-全部；01-火灾预警；02-人脸识别；03-任务预警；04-人流聚集）
            String eventType = (String)map.get("eventType");
            if("all".equals(eventType)){
                eventType="";
            }
            auth.setSearchOne(eventType);
            //01-AI预警；02-历史预警
            String[]  searchArr={};
            String type = (String)map.get("type");
            auth.setSearchTwo(type);
            if("01".equals(type)){
                message="AI预警";
                searchArr=new String[]{"00","02"};
                auth.setSearchArr(searchArr);
            }else if("02".equals(type)){
                message="历史预警";
                searchArr=new String[]{"01","03","04"};
                auth.setSearchArr(searchArr);
            }
            //mapList = rmEventInfoMapper.getByType(auth);
            //total=rmEventInfoMapper.getTotal(auth);
            mapList = rmEventInfoMapper.getByTypeEvent(auth);
            if(null!=mapList && mapList.size()>0){
                for(int i=0;i<mapList.size();i++){
                    Map<String, Object> maps = mapList.get(i);
                    String venuesPictures = (String) maps.get("venuesPicture");
                    if(null!=venuesPictures && !venuesPictures.isEmpty()){
                        List<Map<String, Object>> path = rmFileMapper.getPath(venuesPictures.split(","));
                        if(null!=path && path.size()>0){
                            maps.put("venuesPicture",path.get(0).get("filePath"));
                        }
                    }
                }
            }
            total=rmEventInfoMapper.getByTypeEventTotal(auth);

            code=ResultCode.SUCCESS.getCode();
            message=message+"，查询成功";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message=message+"，查询失败";
            e.printStackTrace();
        }
        return new AppResponse(code,message,total,mapList.toArray());
    }

    @Override
    public AppResponse getAllNum(String type,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="预警事件数量统计！";
        List<Map<String, Object>> allNum = new ArrayList<>();
        try {
            ParamsVo auth = this.getAuth(token);
            auth.setSearchTwo(type);
            if("01".equals(type)){
                message="AI预警";
                auth.setSearchArr(new String[]{"00","02"});
            }else if("02".equals(type)){
                message="历史预警";
                auth.setSearchArr(new String[]{"01","03","04"});
            }
            allNum = rmEventInfoMapper.getAllNum(auth);

            code= ResultCode.SUCCESS.getCode();
            message="预警事件数量统计成功！";
        }catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message="预警事件数量统计失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,allNum.toArray());
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
            ParamsVo auth = new ParamsVo();
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(ParamCode.EVENT_STATE_00.getCode());
            auth.setSearchTwo("");
            String login = this.getLogin(token);
            auth.setSearchThree(login);

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

    /**
     * 接受NB烟感器的数据:默认传来的都是火警
     * @param eventEntity
     * @return
     */
    @Override
    public OutInterfaceResponse addEventByNB(String eventEntity) {
        long code=ResultCode.FAILED.getCode();
        String message="接受NB烟感器的数据:"+eventEntity;
        System.out.println(message);
        int relVenuesId=10000001;//后期提供
        try {

            //数据解析
            Map<String, Object> map = JsonUtils.jsonToMap(eventEntity);
            EventEntity event = new EventEntity();
            String deviceType = (String) map.get("deviceType");//设备类型
            String deviceId = (String) map.get("deviceId");
            String deviceName = (String) map.get("deviceName");//设备名字
            String at = (String) map.get("at");//事发时间
            String type = (String) map.get("type");//事件类型   "alarm": 告警；"clean": 告警清除；"reset": 设备重置；"dat": 数据上报
            String data = (String) map.get("data");//消息内容
            double level = (double) map.get("level");//消息  事件=0,次要=1,重要=2,严重=3, 不填为严重。（火警默认严重)
            int intValue = (int) level;
            String location = (String) map.get("location");//位置
            String rawData = (String) map.get("rawData");//原始数据
            //获取场所
            String venuesId= (String) map.get("venuesId");
            if(null==venuesId || venuesId.isEmpty()){

            }else{
                relVenuesId = Integer.parseInt(venuesId);
            }
            //数据封装
            event.setDeviceName(deviceName);
            event.setAccessNumber(deviceId);
            event.setDeviceType(deviceType);
            event.setWarnTime(TimeTool.getYmdHms());
            event.setEventType(ParamCode.EVENT_TYPE_01.getCode());//默认传来的都是火警
            event.setRawData(rawData);
            event.setEventData(data);
            event.setEventLevel("3");//火警默认严重
            event.setLocation(location);
            event.setEventState("00");
            event.setHandleResults("待处理");
            event.setRelVenuesId(relVenuesId);

            Timestamp timestamp = new Timestamp(new Date().getTime());
            String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            event.setHandleTime(format);
            event.setEventResource(ParamCode.EVENT_FILE_02.getCode());//烟感
            //1.事件数据保存
            rmEventInfoMapper.addEventByNB(event);

            //2.发送短信通知
            //火警全员通知
            this.addNotifiedParty(ParamCode.EVENT_TYPE_01.getCode(),relVenuesId,event.getEventId(),location);

            code=ResultCode.SUCCESS.getCode();
            message="NB烟感器数据处理成功！";
        }catch (RuntimeException e){
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e){
            message="NB烟感器数据处理失败！";
            e.printStackTrace();
        }

        return new OutInterfaceResponse(code,message);
    }

    @Override
    public AppResponse getEventsByState(Integer page, Integer size, String eventState,String token) {

        long code= ResultCode.SUCCESS.getCode();
        String message="";
        AppResponse bean = new AppResponse();
        List<Map<String, Object>> mapList=new ArrayList<>();
        try {


            //参数封装
            ParamsVo auth = new ParamsVo();
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            auth.setPage(page);
            auth.setSize(size);
            if("00".equals(eventState)){//通知
                auth.setSearchOne("00");
            }else if("01".equals(eventState)){//历史推送
                auth.setSearchTwo("00");
            }
            String login = this.getLogin(token);
            auth.setSearchThree(login);

            mapList = rmEventInfoMapper.getEventsByState(auth);
            Object[] objects = mapList.toArray();
            Long total=rmEventInfoMapper.getTotalByState(auth);
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
    public AppResponse dismissEvent(String eventId,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="误报解除失败！";
        try {
            //数据校验
            this.checkEvent(eventId);
            //更新预警事件表
            EventEntity ev=new EventEntity();
            ev.setEventId(Integer.parseInt(eventId));
            ev.setEventState(ParamCode.EVENT_STATE_04.getCode());
            ev.setHandleResults("处理人："+this.getLogin(token));
            ev.setHandleTime(TimeTool.getYmdHms());
            rmEventInfoMapper.updateEventState(ev);
            //更新通知
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_04.getCode());

            code= ResultCode.SUCCESS.getCode();
            message="误报解除成功";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message="误报解除失败";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse callFire(String eventId,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="拨打119";
        try {
            //数据校验
            this.checkEvent(eventId);
            //更新预警事件表
            EventEntity ev=new EventEntity();
            ev.setEventId(Integer.parseInt(eventId));
            ev.setEventState(ParamCode.EVENT_STATE_03.getCode());
            ev.setHandleResults("处理人："+this.getLogin(token));
            ev.setHandleTime(TimeTool.getYmdHms());
            //更新预警事件表
            rmEventInfoMapper.updateEventState(ev);
            //更新通知
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_03.getCode());
            message="拨打119成功";
            code= ResultCode.SUCCESS.getCode();
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message="拨打119失败";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse reportOne(String eventId,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="一键上报（场所管理人员）";
        try {
            //数据校验
            EventEntity event = this.checkEvent(eventId);
            //任务
            TaskEntity taskEntity=new TaskEntity();
            taskEntity.setTaskType(event.getEventType());
            taskEntity.setEndTime(TimeTool.strYmdHmsToDate(event.getWarnTime()));
            taskEntity.setTaskName("预警事件：一键上报（场所管理人员）");
            taskEntity.setTaskContent("预警事件,请处理");
            taskEntity.setRelVenuesId(String.valueOf(event.getRelVenuesId()));
            taskEntity.setEmergencyLevel(event.getEventLevel());
            taskEntity.setRelEventId(eventId);
            //获取登录用户信息
            String login = this.getLogin(token);
            List<String> userList =new ArrayList<>();
            //获取下节点处理人
            //1.2.管理人员,根据场所找三人组组员
            List<Map<String, Object>> sanByVenues = sysUserMapper.getSanByVenues(event.getRelVenuesId());
            if(null!=sanByVenues && sanByVenues.size()>0){
                for(int i=0;i<sanByVenues.size();i++){
                    Map<String, Object> map = sanByVenues.get(i);
                    String userMobile = (String) map.get("userMobile");
                    userList.add(userMobile);
                }
            }

            //发起流程-发起任务
            this.launch(taskEntity,userList,login,"06");

            //更新预警事件表
            EventEntity ev=new EventEntity();
            ev.setEventId(Integer.parseInt(eventId));
            ev.setEventState(ParamCode.EVENT_STATE_02.getCode());
            ev.setHandleResults("处理人："+this.getLogin(token));
            ev.setHandleTime(TimeTool.getYmdHms());
            rmEventInfoMapper.updateEventState(ev);
            //更新通知
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_02.getCode());

            message="一键上报成功";
            code= ResultCode.SUCCESS.getCode();
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="一键上报失败";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    public AppResponse reportOneJg(String eventId, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="一键上报（监管）";
        try {
            //数据校验
            EventEntity event = this.checkEvent(eventId);
            //任务
            TaskEntity taskEntity=new TaskEntity();
            taskEntity.setTaskType(event.getEventType());
            taskEntity.setEndTime(TimeTool.strYmdHmsToDate(event.getWarnTime()));
            taskEntity.setTaskName("预警事件：一键上报（监管）");
            taskEntity.setTaskContent("预警事件,请处理");
            taskEntity.setRelVenuesId(String.valueOf(event.getRelVenuesId()));
            taskEntity.setEmergencyLevel(event.getEventLevel());
            taskEntity.setRelEventId(eventId);
            //获取登录用户信息
            String login = this.getLogin(token);
            List<String> userList =new ArrayList<>();
            //获取下节点处理人
            //1.1.监管
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
            if(null!=sysUserEntity){
                String identify = sysUserEntity.getIdentity();
                //根据用户身份，查询
                if("10000006".equals(identify) || "10000007".equals(identify)){
                    //查找街干事、街委员
                    List<SysUserEntity> jie = sysUserMapper.getJie(login, identify);
                    if(jie.size()>0 && null!=jie){
                        for(int i=0;i<jie.size();i++){
                            String userMobile = jie.get(i).getUserMobile();
                            userList.add(userMobile);
                        }
                    }
                }else if("10000004".equals(identify) || "10000005".equals(identify)){
                    //查找区干事、区委员
                    List<SysUserEntity> qu = sysUserMapper.getQu(login, identify);
                    if(qu.size()>0 && null!=qu){
                        for(int i=0;i<qu.size();i++){
                            String userMobile = qu.get(i).getUserMobile();
                            userList.add(userMobile);
                        }
                    }
                }
            }else{
                throw new RuntimeException("系统中不存在下节点处理人，请先添加！");
            }

            //发起流程-发起任务
            this.launch(taskEntity,userList,login,"05");

            //更新预警事件表
            EventEntity ev=new EventEntity();
            ev.setEventId(Integer.parseInt(eventId));
            ev.setEventState(ParamCode.EVENT_STATE_02.getCode());
            ev.setHandleResults("处理人："+this.getLogin(token));
            ev.setHandleTime(TimeTool.getYmdHms());
            rmEventInfoMapper.updateEventState(ev);
            //更新通知
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_02.getCode());

            message="一键上报成功";
            code= ResultCode.SUCCESS.getCode();
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="一键上报失败";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    public AppResponse getEventsMonth(int num,String type,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="月统计事件";
        List<Map<String, Object>> eventsMonth=new ArrayList<>();
        try {
            //权限参数
            ParamsVo auth = this.getAuth(token);
            auth.setSearchOne(type);
            auth.setSize(num);
            //调用
            eventsMonth = rmEventInfoMapper.getEventsMonth(auth);
            code= ResultCode.SUCCESS.getCode();
            message="月统计事件成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="月统计事件失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsMonth.toArray());
    }

    @Override
    public AppResponse getEventsDay(int num,String type,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="天统计事件";
        List<Map<String, Object>> eventsDay=new ArrayList<>();
        try {
            //权限参数
            ParamsVo auth = this.getAuth(token);
            auth.setSearchOne(type);
            auth.setSize(num);
            //调用
            eventsDay = rmEventInfoMapper.getEventsDay(auth);
            code= ResultCode.SUCCESS.getCode();
            message="天统计事件成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="天统计事件失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsDay.toArray());
    }

    @Override
    public AppResponse getEventsWeek(int num, int dayNum,String type,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="周统计事件";
        List<Map<String, Object>> eventsWeek=new ArrayList<>();
        try {
            //权限参数
            ParamsVo auth = this.getAuth(token);
            auth.setSearchOne(type);
            auth.setSize(num);
            //调用
            eventsWeek = rmEventInfoMapper.getEventsWeek(auth);
            code= ResultCode.SUCCESS.getCode();
            message="周统计事件成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="周统计事件失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsWeek.toArray());
    }

    @Override
    public AppResponse getEventsGather(int num,String dateType,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="统计事件汇总获取失败！";
        List<Map<String, Object>> eventsGather=new ArrayList<>();
        try {
            //权限参数
            ParamsVo auth = this.getAuth(token);
            //auth.setSearchOne(dateType);
            auth.setSize(num);
            //调用
            if("month".equals(dateType)){
                eventsGather = rmEventInfoMapper.getEventsMonthGather(auth);
            }else if("week".equals(dateType)){
                //int dayNum=7*(num+1)-1;
                eventsGather = rmEventInfoMapper.getEventsWeekGather(auth);
            }else if("day".equals(dateType)){
                eventsGather = rmEventInfoMapper.getEventsDayGather(auth);
            }
            code= ResultCode.SUCCESS.getCode();
            message="统计事件汇总获取成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
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
    public void addNotifiedParty(String eventType,int relVenuesId,int relEventId,String location) {

        String contents="【云监控中心】您好！位于"+location+"疑似发生火灾，请您立刻前去处理！！";
        String user="";//监管
        String manager="";// 管理

        NotifiedEntity notifiedEntity=new NotifiedEntity();
        //1.根据场所获取场所三人驻堂的成员
        List<Map<String, Object>> userList = sysUserMapper.getSanByVenues(relVenuesId);

        if(null!=userList && userList.size()>0){
            for(int i=0;i<userList.size();i++){
                Map<String, Object> map = userList.get(i);
                String userMobile = (String) map.get("userMobile");
                user=user+userMobile+",";
                //短信通知
                //String message = SendMassage.sendSms(contents, userMobile);
                String message ="";
                System.out.println(userMobile+message+"，共发送"+(i+1)+"条短信");
            }
            notifiedEntity.setNotifiedUser(user);
        }else{
            throw new RuntimeException("该场所内尚未添加三人驻堂成员！");
        }

        //2.火灾预警全员推送，火警信息默认推送给该场所相关的所有工作人员，（工作人员包含三人驻堂的成员，和该场所相关的管理人员）
        if(ParamCode.EVENT_TYPE_01.getCode().equals(eventType)){
            //根据场所获取场所相关的教职人员
            manager = rmStaffInfoMapper.getManagerByVenuesId(relVenuesId);
            if(null!=manager && !manager.isEmpty()){
                notifiedEntity.setNotifiedManager(manager);
                //短信通知
                String[] split = manager.split(",");
                for(int i=0;i<split.length;i++){
                    String managerMobile = split[i];
                    //String message = SendMassage.sendSms(contents, managerMobile);
                    String message ="";
                    System.out.println(managerMobile+message+"，共发送"+(i+1)+"条短信");
                }
            }else{
                throw new RuntimeException("该场所内尚未添加职员信息！");
            }
        }
        //保存
        notifiedEntity.setRefEventId(relEventId);
        notifiedEntity.setNotifiedFlag(ParamCode.NOTIFIED_FLAG_01.getCode());
        notifiedEntity.setNotifiedTime(new Date());
        eventNotifiedMapper.addNotified(notifiedEntity);
        //普通预警仅上报才给领导推送

        log.info(relEventId+"预警信息已通知：三人驻堂的成员（"+user+");管理人员（"+manager+")");
    }

    @Transactional
    public String launch(TaskEntity taskEntity,List<String> userList,String loginNm,String type) {

        Authentication.setAuthenticatedUserId(loginNm);
        //inputUser就是在bpmn中Assignee配置的参数
        Map<String, Object> variables = new HashMap<>();
        variables.put("handleList2",userList );
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
        String procInstId=tmp.getProcessInstanceId();
        //taskService.setAssignee("assignee2",userNbr);
        taskService.complete(tmp.getId(),variables);

        //保存任务信息
        taskEntity.setLaunchPerson(loginNm);
        taskEntity.setLaunchTime(new Date());
        taskEntity.setProcInstId(procInstId);
        if("05".equals(type)){
            taskEntity.setTaskType(TaskParamsEnum.TASK_FLOW_TYPE_05.getName());
            taskEntity.setFlowType(TaskParamsEnum.TASK_FLOW_TYPE_05.getCode());
        }else if("06".equals(type)){
            taskEntity.setTaskType(TaskParamsEnum.TASK_FLOW_TYPE_06.getName());
            taskEntity.setFlowType(TaskParamsEnum.TASK_FLOW_TYPE_06.getCode());
        }
        taskInfoMapper.addTask(taskEntity);
        log.info("任务id："+processInstanceId+" 发起申请，任务开始！");

        return "流程id(唯一标识)procInstId:"+procInstId;
    }


    /**
     * 预警校验
     * @param eventId
     */
    public EventEntity checkEvent(String eventId){
        ParamsVo vo=new ParamsVo();
        vo.setSearchOne(eventId);
        List<EventEntity> eventByVo = rmEventInfoMapper.getEventByVo(vo);
        EventEntity event =new EventEntity();
        if(null!=eventByVo && eventByVo.size()>0){
            event = eventByVo.get(0);
            String eventState = event.getEventState();
            if(ParamCode.EVENT_STATE_01.getCode().equals(eventState)){
                throw new RuntimeException("该预警"+ParamCode.EVENT_STATE_01.getMessage());
            }else if(ParamCode.EVENT_STATE_02.getCode().equals(eventState)){
                throw new RuntimeException("该预警已"+ParamCode.EVENT_STATE_02.getMessage());
            }else if(ParamCode.EVENT_STATE_03.getCode().equals(eventState)){
                throw new RuntimeException("该预警已"+ParamCode.EVENT_STATE_03.getMessage());
            }else if(ParamCode.EVENT_STATE_04.getCode().equals(eventState)){
                throw new RuntimeException("该预警已"+ParamCode.EVENT_STATE_04.getMessage());
            }
        }else{
            throw new RuntimeException("预警信息丢失，请联系管理员");
        }
        return event;
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
