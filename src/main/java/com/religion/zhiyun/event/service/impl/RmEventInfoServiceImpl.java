package com.religion.zhiyun.event.service.impl;

import com.religion.zhiyun.event.dao.EventNotifiedMapper;
import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.AiEntity;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.entity.EventReportMenEntity;
import com.religion.zhiyun.event.entity.NotifiedEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.interfaces.service.AiEventService;
import com.religion.zhiyun.login.entity.LoginInfo;
import com.religion.zhiyun.monitor.dao.MonitorBaseMapper;
import com.religion.zhiyun.monitor.dao.MonitorSmokerMapper;
import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.base.dao.SysBaseMapper;
import com.religion.zhiyun.sys.base.enums.SysBaseEnum;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.CommentEntity;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.OutInterfaceResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.sms.SendMassage;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
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
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class RmEventInfoServiceImpl implements RmEventInfoService {
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
    RmVenuesInfoMapper rmVenuesInfoMapper;

    @Autowired
    MonitorBaseMapper monitorBaseMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    SysBaseMapper sysBaseMapper;

    @Autowired
    MonitorSmokerMapper monitorSmokerMapper;
    @Autowired
    private AiEventService aiEventService;

    @Override
    public AppResponse addAiEvent(String eventJson) {
        log.info("AI告警接收:"+eventJson);
        long code=ResultCode.FAILED.getCode();
        String message="AI告警,数据处理失败！";

        try {
            EventEntity event=new EventEntity();
            event.setEventData(eventJson);

            AiEntity dataEntity = JsonUtils.jsonTOBean(eventJson, AiEntity.class);
            String data = dataEntity.getData();
            AiEntity aiEntity = JsonUtils.jsonTOBean(data, AiEntity.class);
            event.setEventResource(ParamCode.EVENT_FILE_01.getCode());
            //2.预警信息处理
            event.setWarnTime(TimeTool.getYmdHms());
            //“人员聚集”、“发现重点人员”、“明火检测” 01-明火;02-超限;03-重点;04-集聚
            //程度
            String eventLevel=ParamCode.EVENT_LEVEL_02.getCode();
            String content = aiEntity.getContent();
            event.setDeviceCode(content);
            String eventType="";
            String cont="";
            if(content.contains("明火")){
                eventType=ParamCode.EVENT_TYPE_01.getCode();
                eventLevel=ParamCode.EVENT_LEVEL_01.getCode();
                cont=ParamCode.EVENT_TYPE_01.getMessage();
            }else if(content.contains("重点")){
                eventType=ParamCode.EVENT_TYPE_03.getCode();
                cont=ParamCode.EVENT_TYPE_03.getMessage();
            }else if(content.contains("聚集")){
                eventType=ParamCode.EVENT_TYPE_04.getCode();
                cont=ParamCode.EVENT_TYPE_04.getMessage();
            }else if(content.contains("未成年")){//未成年检测
                eventType=ParamCode.EVENT_TYPE_03.getCode();
                cont=ParamCode.EVENT_TYPE_03.getMessage();
            }else{
                eventType="0";
            }
            //预警类型
            event.setEventType(eventType);
            event.setEventLevel(eventLevel);//普通
            event.setEventState(ParamCode.EVENT_STATE_03.getCode());
            //获取场所id
            String deviceId = aiEntity.getDeviceId();//监控通道编码
            String venue = monitorBaseMapper.getVenue(deviceId);
            if(GeneTool.isEmpty(venue)){
                throw  new RuntimeException("AI预警通道："+deviceId+"场所信息不存在，请联系管理员！");
            }
            int relVenuesId = Integer.parseInt(venue);
            event.setRelVenuesId(relVenuesId);
            event.setHandleResults("预警已发起！");
            event.setHandleTime(TimeTool.getYmdHms());
            event.setAccessNumber(deviceId);//设备编码

            String deviceName = aiEntity.getDeviceName();
            event.setDeviceName(deviceName);
            String title = aiEntity.getTitle();
            event.setDeviceType(title);
            //图片处理
            String eventFile = aiEntity.getEventFile();
            FileEntity fileEntity=new FileEntity();
            fileEntity.setFilePath(eventFile);
            fileEntity.setFileType(ParamCode.FILE_TYPE_01.getCode());
            fileEntity.setCreator("AI预警图片");
            fileEntity.setCreateTime(TimeTool.getYmdHms());
            //AI图片下载
           /* AppResponse appResponse = aiEventService.downImage(eventFile);
            if(200==appResponse.getCode()){
                String direct = appResponse.getDirect();
                fileEntity.setImgPath(direct);
            }else{
                fileEntity.setImgPath(appResponse.getMessage());
            }*/
            rmFileMapper.add(fileEntity);

            int fileId = fileEntity.getFileId();
            event.setPicturesPath(String.valueOf(fileId));
            String eventPlaceName = aiEntity.getEventPlaceName();//地址
            event.setLocation(eventPlaceName);
            //事件新增
            rmEventInfoMapper.addEvent(event);
            int eventId = event.getEventId();
            //3.短信通知，新增通知任务发起
            VenuesEntity venues = rmVenuesInfoMapper.getVenueByID(venue);
            String venuesAddres ="";
            String venuesName ="";
            if(null!=venues){
                venuesAddres = venues.getVenuesAddres();
                venuesName = venues.getVenuesName();
            }
            String contents="【瓯海宗教智治】您好！位于"+venuesAddres+"的"+venuesName+",触发“"+cont+"”预警，请您立刻前去处理！！";
            HashMap<String,Object> mapCall=new HashMap<>();
            mapCall.put("eventType",eventType);
            mapCall.put("relVenuesId",relVenuesId);
            mapCall.put("eventId",eventId);
            mapCall.put("contents",contents);
            mapCall.put("eventLevel",eventLevel);
            mapCall.put("venuesAddres",venuesAddres);
            mapCall.put("venuesName",venuesName);
            mapCall.put("event",cont);

            this.addNotifiedParty(mapCall);

           /* //查询数据库数据是否存在，不存在新增；存在，修改
            EventEntity eventEntity = rmEventInfoMapper.queryEvent(event);
            int eventId =0;
            if(null!=eventEntity){
                //更新
                eventId = eventEntity.getEventId();
                //rmEventInfoMapper.updateEvent(eventEntity);
            }else{

            }*/

            code=ResultCode.SUCCESS.getCode();
            message="AI告警,数据处理成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public OutInterfaceResponse addEvent(String eventJson) {
        System.out.println("AI告警:"+eventJson);
        log.info("AI告警:"+eventJson);
        long code=ResultCode.FAILED.getCode();
        String message="AI告警,数据处理！";

        EventEntity event=new EventEntity();
        int relVenuesId=0;//需要场所名称，获取场所信息
        try{
            Map<String, Object> map = JsonUtils.jsonToMap(eventJson);

            //Long id= (Long) map.get("id");
            String taskId= (String) map.get("taskId");//任务id
            String alarmName= (String) map.get("alarmName");//告警名称
            String alarmCode= (String) map.get("alarmCode");//预警类型编码
            String alarmLevelName= (String) map.get("alarmLevelName");//告警等级 程度：（一般）
            String algoUuid= (String) map.get("algoUuid");//算法uuid

            String cameraId= (String) map.get("cameraId");//摄像头编号
            String cameraName= (String) map.get("cameraName");//摄像头名称
            String locationName= (String) map.get("locationName");//摄像头位置

            List<String> pictureRecs= (List<String>) map.get("pictureRecs");//所有带框图⽚地址
            String timeStamp= (String) map.get("timeStamp");//告警产⽣时间
            String videoSource= (String) map.get("videoSource");//告警来源
            String latLong= (String) map.get("latLong");//经纬度信息（不存在则为空串）
            String extType= (String) map.get("extType");//只有机动⻋超时违停和⼈流检测存在这个字段
            //String extData= (String) map.get("extData");//只有机动⻋超时违停和⼈流检测存在这个字段
            String infoSource= (String) map.get("infoSource");//信息来源（移动通信）

            //后增
            String deviceName=(String) map.get("deviceName");//设备名称
            String deviceId=(String) map.get("deviceId");//设备编码
            String alarmId=(String) map.get("alarmId");//告警事件ID

            /*String picture= (String) map.get("picture");
            List<String> pictures= (List<String>) map.get("pictures");
            String pictureRec= (String) map.get("pictureRec");
            String videoUrl= (String) map.get("videoUrl");
            String streetName= (String) map.get("streetName");//街道
            String algoName= (String) map.get("algoName");*/
            //String durationTime= (String) map.get("durationTime");

            //获取场所
            String venue = monitorBaseMapper.getVenue(deviceId);
            if(GeneTool.isEmpty(venue)){
                throw  new RuntimeException("未找到关联场所，请联系管理员！");
            }else{
                relVenuesId = Integer.parseInt(venue);
            }

           /* //1.摄像设备处理
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
                   *//* monitorsList.get(0)
                    monitors.setMonitorUrl(videoUrl);
                    monitors.setVenuesAddres(locationName);
                    //需要场所名称，获取场所信息
                    monitors.setRelVenuesId(relVenuesId);
                    monitors.setState( ParamCode.MONITOR_STATE_01.getCode());//在线
                    monitors.setFunctionType(infoSource);
                    monitors.setLastModifier("AI告警");
                    monitors.setLastModifyTime(timeStamp);
                    rmMonitroInfoMapper.updateMonitro(monitors);*//*
                }
            }*/

            //2.预警信息处理
            event.setAccessNumber(cameraId);
            event.setWarnTime(timeStamp);
            //预警类型
            /*if("TRASH_ACCUMULATION".equals(alarmCode)){
                event.setEventType(ParamCode.EVENT_TYPE_02.getCode());
            }else if("CROWDS_GATHER".equals(alarmCode)){
                event.setEventType(ParamCode.EVENT_TYPE_04.getCode());
            }else if("FIR".equals(alarmCode)){
                event.setEventType(ParamCode.EVENT_TYPE_01.getCode());
            }else{
                event.setEventType(ParamCode.EVENT_TYPE_03.getCode());
            }*/

            event.setEventType(alarmCode);
            //程度
            /*String emergencyLevel="02";
            if("轻微".equals(alarmLevelName)){
                event.setEventLevel("1");//普通
            }else if("一般".equals(alarmLevelName)){
                event.setEventLevel("2");//普通
            }else if("严重".equals(alarmLevelName)){
                event.setEventLevel("3");//紧急
                emergencyLevel="01";
            }else{
                event.setEventLevel("0");//普通
            }*/
            event.setEventLevel(alarmLevelName);//普通
            event.setEventState(ParamCode.EVENT_STATE_03.getCode());
            event.setRelVenuesId(relVenuesId);
            event.setHandleResults("0");
            event.setHandleTime(timeStamp);
            event.setDeviceCode(cameraId);//设备编码
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
                //3.短信通知，新增通知任务发起
                HashMap<String,Object> mapj=new HashMap<>();
                mapj.put("eventType",event.getEventType());
                mapj.put("relVenuesId",relVenuesId);
                mapj.put("eventId",eventId);
                mapj.put("contents","");
                mapj.put("eventLevel",alarmLevelName);
                mapj.put("venuesAddres","");
                mapj.put("venuesName","");
                mapj.put("event","");
                this.addNotifiedParty(mapj);
            }

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
                searchArr=new String[]{"02","03"};
                auth.setSearchArr(searchArr);
                auth.setSearchThree("01");
            }else if("02".equals(type)){
                message="历史预警";
                searchArr=new String[]{"01","04","05"};
                auth.setSearchArr(searchArr);
                auth.setSearchFour("02");
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
                auth.setSearchArr(new String[]{"02","03"});
            }else if("02".equals(type)){
                message="历史预警";
                auth.setSearchArr(new String[]{"01","04","05"});
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
    public RespPageBean getEventsByPage(Map<String, Object> map,String token) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        Long total=0l;
        long code= ResultCode.FAILED.getCode();
        String result="预警查询pc！";
        try {
            String accessNumber = (String)map.get("accessNumber");
            String venuesName = (String)map.get("venuesName");
            String eventType = (String)map.get("eventType");
            String eventState = (String)map.get("eventState");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            ParamsVo auth = this.getAuth(token);
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(accessNumber);
            auth.setSearchTwo(venuesName);
            auth.setSearchThree(eventType);
            auth.setSearchFour(eventState);
            if("02".equals(eventState)){
                auth.setSearchArr(new String[]{"01","04","05"});
            }else{
                auth.setSearchArr(new String[]{"02","03"});
            }

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
            //auth.setSearchOne(ParamCode.EVENT_STATE_02.getCode());
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
            if(null!=eventDetail && eventDetail.size()>0){
                Map<String, Object> map = eventDetail.get(0);
                List<Map<String, Object>> eventReportMen = rmEventInfoMapper.getEventReportMen(eventId);
                map.put("reportMen",eventReportMen.toArray());
            }

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
        int relVenuesId=0;//后期提供
        String emergencyLevel="";
        try {

            //数据解析
            Map<String, Object> map = JsonUtils.jsonToMap(eventEntity);

            String deviceType = (String) map.get("deviceType");//设备类型
            String deviceId = (String) map.get("deviceId");
            String deviceName = (String) map.get("deviceName");//设备名字
            String at = (String) map.get("at");//事发时间
            String type = (String) map.get("type");//事件类型   "alarm": 告警；"clean": 告警清除；"reset": 设备重置；"dat": 数据上报
            String streamId=(String) map.get("streamId");
            String data = (String) map.get("data");//消息内容
            double level = (double) map.get("level");//消息  事件=0,次要=1,重要=2,严重=3, 不填为严重。（火警默认严重)
            String sValue = String.valueOf((int)level);
            if("2".equals(sValue)){
                emergencyLevel="01";
            }
            String location = (String) map.get("location");//位置
            String rawData = (String) map.get("rawData");//原始数据

            //获取场所
            String venuesId = monitorSmokerMapper.getVenue(deviceId);
            if(!GeneTool.isEmpty(venuesId)){
                relVenuesId = Integer.parseInt(venuesId);
            }else{
                throw new RuntimeException("该场所烟感信息未配置，请联系烟感系统处理！");
            }

            //数据封装
            if("alarm".equals(type) && "fireAlarm".equals(streamId) && 2==level){
                EventEntity event = new EventEntity();
                event.setDeviceName(deviceName);
                event.setAccessNumber(deviceId);
                event.setDeviceType(deviceType);
                event.setWarnTime(TimeTool.getYmdHms());
                event.setEventType(ParamCode.EVENT_TYPE_01.getCode());//默认传来的都是火警
                event.setEventData(eventEntity);
                event.setEventLevel("01");//火警默认严重
                event.setLocation(location);
                event.setEventState(ParamCode.EVENT_STATE_03.getCode());
                event.setHandleResults("待处理");
                event.setRelVenuesId(relVenuesId);
                event.setDeviceCode(streamId);

                Timestamp timestamp = new Timestamp(new Date().getTime());
                String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
                event.setHandleTime(format);
                event.setEventResource(ParamCode.EVENT_FILE_02.getCode());//烟感
                //1.事件数据保存
                rmEventInfoMapper.addEventByNB(event);

                //2.发送短信通知:火警全员通知
                VenuesEntity venues = rmVenuesInfoMapper.getVenueByID(venuesId);
                String venuesAddres ="";
                String venuesName ="";
                if(null!=venues){
                    venuesAddres = venues.getVenuesAddres();
                    venuesName = venues.getVenuesName();
                }
                String contents="【瓯海宗教智治】您好！位于"+venuesAddres+"的"+venuesName+",触发“烟感”预警，请您立刻前去处理！！";
                HashMap<String,Object> mapCall=new HashMap<>();
                mapCall.put("eventType",ParamCode.EVENT_TYPE_01.getCode());
                mapCall.put("relVenuesId",relVenuesId);
                mapCall.put("eventId",event.getEventId());
                mapCall.put("contents",contents);
                mapCall.put("eventLevel",emergencyLevel);
                mapCall.put("venuesAddres",venuesAddres);
                mapCall.put("venuesName",venuesName);
                mapCall.put("event","烟感");
                this.addNotifiedParty(mapCall);

                code=ResultCode.SUCCESS.getCode();
                message="NB烟感器数据处理成功！";
            }else{
                code=ResultCode.SUCCESS.getCode();
                message="type:"+type+";streamId"+streamId+";level:"+level+"：NB烟感器数据不处理！";
            }

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
            String[]  searchArr={};
            if("01".equals(eventState)){
                message="通知预警";
                searchArr=new String[]{"02","03"};
                auth.setSearchArr(searchArr);
            }else if("02".equals(eventState)){
                message="历史预警";
                searchArr=new String[]{"01","04","05"};
                auth.setSearchArr(searchArr);
            }
            String login = this.getLogin(token);
            auth.setSearchOne(login);
            mapList = rmEventInfoMapper.getEventsByState(auth);
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
            //this.checkEvent(eventId);
            //任一人解除误报后预警任务结束，在预警信息展示解除误报人和解除时间、上报人
            //查询任务，如果存在做结束处理
            List<TaskEntity> taskByEventId = taskInfoMapper.getTaskByEventId(eventId);
            if(null!=taskByEventId && taskByEventId.size()>0){
                for(int i=0;i<taskByEventId.size();i++){
                    TaskEntity taskEntity = taskByEventId.get(i);
                    String procInstId = taskEntity.getProcInstId();
                    Map<String, Object> map =new HashMap<>();
                    map.put("procInstId",procInstId);
                    map.put("handleResults","1");
                    map.put("feedBack","误报解除");
                    map.put("picture","");
                    map.put("eventSta",ParamCode.NOTIFIED_FLAG_04.getCode());
                    this.reportOneHandle(map,token);
                }
            }
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
    public AppResponse mzResponse(String eventId,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="民宗快响推送失败！";
        try {
            //结束任务
            List<TaskEntity> taskByEventId = taskInfoMapper.getTaskByEventId(eventId);
            if(null!=taskByEventId && taskByEventId.size()>0){
                for(int i=0;i<taskByEventId.size();i++){
                    TaskEntity taskEntity = taskByEventId.get(i);
                    String procInstId = taskEntity.getProcInstId();
                    Map<String, Object> map =new HashMap<>();
                    map.put("procInstId",procInstId);
                    map.put("handleResults","1");
                    map.put("feedBack","民宗快响推送");
                    map.put("picture","");
                    map.put("eventSta",ParamCode.NOTIFIED_FLAG_05.getCode());
                    this.reportOneHandle(map,token);
                }
            }
            code= ResultCode.SUCCESS.getCode();
            message="民宗快响推送成功";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message="民宗快响推送失败";
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
            /*//数据校验
            this.checkEvent(eventId);
            //更新预警事件表
            EventEntity ev=new EventEntity();
            ev.setEventId(Integer.parseInt(eventId));
            ev.setEventState(ParamCode.EVENT_STATE_03.getCode());
            ev.setHandleResults(this.getLogin(token));
            ev.setHandleTime(TimeTool.getYmdHms());
            //更新预警事件表
            rmEventInfoMapper.updateEventState(ev);
            //更新通知
            eventNotifiedMapper.updateNotifiedFlag(eventId,new Date(),ParamCode.NOTIFIED_FLAG_03.getCode());*/
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
    public AppResponse reportOne(String eventId, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="一键上报失败！";
        try {
            String procInstId="";
            List<TaskEntity> taskByEventId = taskInfoMapper.getTaskByEventId(eventId);
            if(null!=taskByEventId && taskByEventId.size()>0){
                procInstId=taskByEventId.get(0).getProcInstId();
            }else{
                throw new RuntimeException("预警事件任务信息丢失，请联系管理员！");
            }

            String login = this.getLogin(token);
            long le= rmEventInfoMapper.queryEventReportMen(login, procInstId);
            if(le==0l){
                //确认是否已上报
                EventReportMenEntity ev=new EventReportMenEntity();
                ev.setRefEventId(eventId);
                ev.setProcInstId(procInstId);
                ev.setReportMen(login);
                ev.setReportTime(TimeTool.getYmdHms());
                rmEventInfoMapper.addEventReportMen(ev);
            }

            message="一键上报成功！";
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
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
    public AppResponse getEventsDay(int num, String type) {
        long code= ResultCode.FAILED.getCode();
        String message="天统计事件";
        List<Map<String, Object>> eventsDay=new ArrayList<>();
        try {
            //权限参数
            ParamsVo auth = new ParamsVo();
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

    @Override
    public AppResponse getDaPingGather(int num, String dateType) {
        long code= ResultCode.FAILED.getCode();
        String message="统计事件汇总获取失败！";
        List<Map<String, Object>> eventsGather=new ArrayList<>();
        try {
            //权限参数
            ParamsVo auth = new ParamsVo();
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

    @Override
    public AppResponse getEventsTrends(int num, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="动态统计事件";
        List<Map<String, Object>> eventsTrends=new ArrayList<>();
        try {
            //权限参数
            ParamsVo auth = this.getAuth(token);
            auth.setSize(num);
            //调用
            auth.setSearchOne("01");
            List<Map<String, Object>> eventsFir = rmEventInfoMapper.getEventsDay(auth);
            auth.setSearchOne("02");
            List<Map<String, Object>> eventsFace = rmEventInfoMapper.getEventsDay(auth);
            auth.setSearchOne("04");
            List<Map<String, Object>> eventsPerson = rmEventInfoMapper.getEventsDay(auth);
            Map<String, Object> map=new HashMap<>();
            map.put("fir",eventsFir.toArray());
            map.put("face",eventsFace.toArray());
            map.put("person",eventsPerson.toArray());

            eventsTrends.add(map);
            code= ResultCode.SUCCESS.getCode();
            message="动态统计事件成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="动态统计事件件失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventsTrends.toArray());
    }

    @Override
    public AppResponse alertEvent(String eventId, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="预警状态查询！";
        String eventState ="";
        try {
            eventState = rmEventInfoMapper.getEventState(eventId);
            code= ResultCode.SUCCESS.getCode();
            message="预警状态查询成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="预警状态查询失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,eventState);
    }


    /**
     * 预警通知保存
     * @param mapCall
     *
     *ai预警推送，监管人员按紧急普通区分，紧急的默认推送所有监管人员，普通的默认推送给街镇、三人驻堂
     *教职人员，按预警的类别进行推送，比如火警可以推送教职人员，但是人脸不一定推送，具体推送什么类型的等跟业主进行确认
     * @return
     */
    public void addNotifiedParty(Map<String,Object> mapCall) {

        /*** 1.参数获取 ***/
        String eventType= (String) mapCall.get("eventType");
        Integer relVenuesId= (Integer) mapCall.get("relVenuesId");
        Integer relEventId= (Integer) mapCall.get("eventId");
        String contents= (String) mapCall.get("contents");
        String emergencyLevel= (String) mapCall.get("eventLevel");

        /*** 2.处理人员查询 ***/
        List<Map<String, Object>> userList =new ArrayList<>();
        //获取通知对象
        if("01".equals(emergencyLevel) || ParamCode.EVENT_TYPE_01.getCode().equals(eventType)){
            //1.根据场所获取场所有相关人员
            userList =sysUserMapper.getAllByVenues(relVenuesId);
        }else{
            //根据场所获取场所三人驻堂、街干
            userList = sysUserMapper.getJgByVenues(relVenuesId);
        }

        /*** 3.预警通知 ***/
        NotifiedEntity notifiedEntity=new NotifiedEntity();
        List<String> userNextList=new ArrayList<>();//节点处理人员
        boolean tmFlag = GeneTool.calendarCompare("08:30", "17:30");
        //3.1.监管人员处理
        String user="";//监管
        if(null!=userList && userList.size()>0){
            for(int i=0;i<userList.size();i++){
                Map<String, Object> map = userList.get(i);
                String userMobile = (String) map.get("userMobile");
                userNextList.add(userMobile);//下节点流程处理人员
                user=user+userMobile+",";//通知人员
                //查询开关，通知
                String openFlag=sysBaseMapper.getOpenState(SysBaseEnum.SEND_MESSAGE_SWITCH.getCode());
                if("1".equals(openFlag)){//1-开；0-关 （短信开关）
                   //3.1.1.电话通知
                    if(tmFlag && ParamCode.EVENT_TYPE_01.getCode().equals(eventType)){
                        mapCall.put("phone",userMobile);
                        /*String sessionId = VoiceCall.voiceCall(mapCall);
                        //保存数据
                        FeeInfo feeInfo =new FeeInfo();
                        feeInfo.setSessionId(sessionId);
                        feeInfo.setEventType(CallEnums.fee.getCode());
                        feeInfo.setRefEventId(String.valueOf(relEventId));
                        eventNotifiedMapper.addCall(feeInfo);*/

                    }
                    //3.1.2.短信通知
                    SendMassage.sendSms(contents, userMobile);
                }
            }
            notifiedEntity.setNotifiedUser(user);
            System.out.println(contents+":共发送"+(userList.size())+"条短信");
        }else{
            throw new RuntimeException("该场所内尚未添加相关成员！");
        }

        //3.2.管理人员
        String manager="";// 管理
        if(ParamCode.EVENT_TYPE_01.getCode().equals(eventType)){
            //根据场所获取场所相关的管理人员
            manager = rmStaffInfoMapper.getManagerByVenuesId(relVenuesId);
            if(null!=manager && !manager.isEmpty()){
                notifiedEntity.setNotifiedManager(manager);
                //短信通知
                String[] split = manager.split(",");
                for(int i=0;i<split.length;i++){
                    String managerMobile = split[i];
                    userNextList.add(managerMobile);//下节点流程处理人员
                    //短信开关
                    String openFlag=sysBaseMapper.getOpenState(SysBaseEnum.SEND_MESSAGE_SWITCH.getCode());
                    if("1".equals(openFlag)){//1-开；0-关
                        //3.2.1.电话通知
                        if(tmFlag){
                            mapCall.put("phone",managerMobile);
                            /*String sessionId = VoiceCall.voiceCall(mapCall);
                            //保存数据
                            FeeInfo feeInfo =new FeeInfo();
                            feeInfo.setSessionId(sessionId);
                            feeInfo.setEventType(CallEnums.fee.getCode());
                            feeInfo.setRefEventId(String.valueOf(relEventId));
                            eventNotifiedMapper.addCall(feeInfo);*/
                        }
                        //3.2.2.短信通知
                        SendMassage.sendSms(contents, managerMobile);
                    }
                }
                System.out.println(contents+":共发送"+(split.length)+"条短信");
            }else{
                throw new RuntimeException("该场所内尚未添加职员信息！");
            }
        }

        /*** 4.保存通知 ***/
        String event= (String) mapCall.get("event");
        notifiedEntity.setEventType(event);//内容
        notifiedEntity.setRefEventId(relEventId);
        notifiedEntity.setNotifiedFlag(ParamCode.NOTIFIED_FLAG_03.getCode());
        notifiedEntity.setNotifiedTime(new Date());
        eventNotifiedMapper.addNotified(notifiedEntity);

        /*** 5.任务发起 ：普通预警仅上报才给领导推送***/
        //5.1.封装任务信息
        TaskEntity taskEntity=new TaskEntity();
        //String eventType = event.getEventType();
        taskEntity.setTaskType(eventType);
        taskEntity.setEndTime("");
        //查询场所名称
        VenuesEntity venueByID = rmVenuesInfoMapper.getVenueByID(String.valueOf(relVenuesId));
        String venuesName = venueByID.getVenuesName();
        String tnm="预警事件：一键上报（监管）";
        if("01".equals(eventType)){
            tnm=venuesName+"-"+ParamCode.EVENT_TYPE_01.getMessage();
        }else if("02".equals(eventType)){
            tnm=venuesName+"-"+ParamCode.EVENT_TYPE_02.getMessage();
        }else if("03".equals(eventType)){
            tnm=venuesName+"-"+ParamCode.EVENT_TYPE_03.getMessage();
        }else if("04".equals(eventType)){
            tnm=venuesName+"-"+ParamCode.EVENT_TYPE_04.getMessage();
        }else if("05".equals(eventType)){
            tnm=venuesName+"-"+ParamCode.EVENT_TYPE_05.getMessage();
        }
        taskEntity.setTaskName(tnm);
        taskEntity.setTaskContent("预警事件,请处理");
        taskEntity.setRelVenuesId(String.valueOf(relVenuesId));
        taskEntity.setEmergencyLevel(emergencyLevel);
        taskEntity.setRelEventId(String.valueOf(relEventId));
        /*//任务处理人
        if(null!=nextList && nextList.size()>0){
            for(int i=0;i<nextList.size();i++){
                Map<String, Object> stringObjectMap = nextList.get(i);
                String userMobile = (String) stringObjectMap.get("userMobile");
                userNextList.add(userMobile);//下节点流程处理人员
            }
        }*/

        //5.2.任务发起
        this.launch(taskEntity,userNextList,"预警平台");

        log.info(relEventId+"预警信息已通知：监管人员（"+user+");管理人员（"+manager+")");
    }

    @Transactional
    public String launch(TaskEntity taskEntity,List<String> userList,String loginNm) {

        Authentication.setAuthenticatedUserId(loginNm);
        Map<String, Object> variables = new HashMap<>();
        //inputUser就是在bpmn中Assignee配置的参数
        if(null!=userList && userList.size()>0){
            variables.put("handleList2",userList );
        }else{
            throw new RuntimeException("无相关处理人，请重新确认！");
        }

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

        //发起人
        taskInfoMapper.updateHiActinst(loginNm,procInstId);

        //保存任务信息
        taskEntity.setLaunchPerson(loginNm);
        taskEntity.setLaunchTime(TimeTool.getYmdHms());
        taskEntity.setProcInstId(procInstId);
        taskEntity.setTaskType(TaskParamsEnum.TASK_FLOW_TYPE_05.getName());
        taskEntity.setFlowType(TaskParamsEnum.TASK_FLOW_TYPE_05.getCode());
        taskInfoMapper.addTask(taskEntity);
        log.info("任务id："+processInstanceId+" 发起申请，任务开始！");

        return "流程id(唯一标识)procInstId:"+procInstId;
    }
    @Override
    @Transactional
    public AppResponse reportOneReport(Map<String, Object> map, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="一键上报任务上报失败！";
        String identify ="";
        try {
            String procInstId = (String)map.get("procInstId");
            if(null==procInstId && procInstId.isEmpty()){
                throw new RuntimeException("流程id为空，请联系管理员！");
            }
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);

            //下节点处理人
            List<String> userList=new ArrayList();
            String notifiedUser="";
            List<Map<String, Object>> mapList=new ArrayList<>();
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
            if(null!=sysUserEntity){
                identify = sysUserEntity.getIdentity();
                //根据用户身份，查询
                if(RoleEnums.JIE_GAN.getCode().equals(identify)){//查找街委
                    mapList = sysUserMapper.getJieWei(loginNm,"");
                }else if(RoleEnums.JIE_WEI.getCode().equals(identify)){//查找区干
                    mapList = sysUserMapper.getQuGan(loginNm,"");
                }else if(RoleEnums.QU_GAN.getCode().equals(identify)){//查找区委员
                    mapList = sysUserMapper.getQuWei(loginNm,"");
                }
                if(null!=mapList && mapList.size()>0){
                    for(int i=0;i<mapList.size();i++){
                        Map<String, Object> mapNext = mapList.get(i);
                        String userMobile = (String) mapNext.get("userMobile");
                        userList.add(userMobile);
                        notifiedUser=notifiedUser+userMobile+",";
                    }
                }
            }else{
                throw new RuntimeException("操作员信息丢失，请联系管理员！");
            }

            if(null==userList && userList.size()<1){
                throw new RuntimeException("系统中不存在下节点处理人，请先添加！");
            }

            //根据角色信息获取自己的待办 act_ru_task
            //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
            //处理自己的待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                Boolean flag=true;
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        flag=false;
                        //Map<String, Object> variables = this.setFlag(sysUserEntity.getIdentity(), "go", userList, procInstId);
                        Map<String, Object> variables = new HashMap<>();
                        if(RoleEnums.JIE_GAN.getCode().equals(identify)){//查找街委
                            variables.put("flag2", "go");
                            variables.put("handleList3", userList);
                        }else if(RoleEnums.JIE_WEI.getCode().equals(identify)){//查找区干
                            variables.put("flag3", "go");
                            variables.put("handleList4", userList);
                        }else if(RoleEnums.QU_GAN.getCode().equals(identify)){//查找区委员
                            variables.put("flag4", "go");
                            variables.put("handleList5", userList);
                        }
                        variables.put("isSuccess", true);
                        //设置本地参数。在myListener1监听中获取。防止审核通过进行驳回
                        taskService.setVariableLocal(item.getId(),"isSuccess",false);
                        //增加审批备注
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),this.getComment(map));
                        //完成此次审批。由下节点审批
                        taskService.complete(item.getId(), variables);
                    }
                }
                //任务已被处理
                if(flag){
                    throw new RuntimeException("任务已被他人上报！");
                }
            }else{
                throw new RuntimeException("任务已被他人处理，流程已结束！！");
            }
            //1.更新事件
            Map<String, Object> evTaDetail = taskInfoMapper.getEvTaDetail(procInstId);
            String eventId =String.valueOf((Integer) evTaDetail.get("eventId")) ;
            EventEntity event=new EventEntity();
            event.setEventId(Integer.parseInt(eventId));
            event.setEventState(ParamCode.EVENT_STATE_02.getCode());
            event.setHandleResults("上报");
            event.setHandleTime(TimeTool.getYmdHms());
            rmEventInfoMapper.updateEventState(event);
            //2.更新通知
            eventNotifiedMapper.updateNotifiedUser(eventId,TimeTool.getYmdHms(),notifiedUser);

            log.info("任务id："+procInstId+" 上报");
            code= ResultCode.SUCCESS.getCode();
            message="一键上报流程上报成功！流程id(唯一标识)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="一键上报流程上报失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse reportOneHandle(Map<String, Object> map, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="一键上报任务处理";
        try {
            String procInstId = (String)map.get("procInstId");
            if(null==procInstId || procInstId.isEmpty()){
                throw new RuntimeException("流程id丢失，请联系管理员！");
            }
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
            String identify ="1001";//教职人员
            if(null!=sysUserEntity){
                identify = sysUserEntity.getIdentity();
            }

            //数据封装
            String eventSta= (String)map.get("eventSta");
            String eventState ="";
            String eventId ="";
            String notice ="";
            String eventType ="";
            String eventLevel ="";
            //根据 procInstId 获取任务
            Map<String, Object> evTaDetail = taskInfoMapper.getEvTaDetail(procInstId);
            if(null!=evTaDetail){
                eventId= String.valueOf((Integer )evTaDetail.get("eventId"));
                eventType= (String) evTaDetail.get("eventType");
                eventLevel= (String) evTaDetail.get("eventLevel");
            }else{
                throw  new RuntimeException("关联事件信息丢失，请联系管理员！");
            }

            //处理待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                Boolean flag=true;
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        flag=false;
                        Map<String, Object> variables =new HashMap<>();
                        if("3".equals(eventLevel) || "01".equals(eventType)){
                            variables.put("flag2", "end");
                        }else{
                            //variables = this.setFlag(sysUserEntity.getIdentity(), "end",null,procInstId);
                            if(RoleEnums.JIE_WEI.getCode().equals(identify)){
                                variables.put("flag3", "end");
                            }else if(RoleEnums.QU_GAN.getCode().equals(identify)) {//查找区委员
                                variables.put("flag4", "end");
                            }else if(RoleEnums.QU_WEI.getCode().equals(identify)){//查找区干
                                variables.put("flag5", "end");
                            }else{
                                variables.put("flag2", "end");
                            }
                        }
                        variables.put("nrOfCompletedInstances", 1);
                        variables.put("isSuccess", true);

                        //设置本地参数。在myListener1监听中获取。
                        taskService.setVariableLocal(item.getId(),"isSuccess",true);
                        //增加审批备注
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),this.getComment(map));
                        //完成此次审批。如果下节点为endEvent。结束流程
                        taskService.complete(item.getId(), variables);
                        log.info("任务id："+procInstId+" 已处理，流程结束！");

                        //更新处理结果
                        TaskEntity taskEntity=new TaskEntity();
                        taskEntity.setHandlePerson(loginNm);
                        taskEntity.setHandleTime(TimeTool.getYmdHms());
                        taskEntity.setHandleResults((String) map.get("handleResults"));
                        taskEntity.setProcInstId(procInstId);
                        taskInfoMapper.updateTask(taskEntity);
                    }
                }
                //任务已被处理
                if(flag){
                    throw new RuntimeException("任务已被他人处理，流程已结束！");
                }
            }else{
                throw new RuntimeException("任务已被他人处理，流程已结束！");
            }

            //修改事件表
            if(ParamCode.EVENT_STATE_04.getCode().equals(eventSta)){
                eventState=ParamCode.EVENT_STATE_04.getCode();
                notice =ParamCode.NOTIFIED_FLAG_04.getCode();
            }if(ParamCode.EVENT_STATE_05.getCode().equals(eventSta)){
                eventState=ParamCode.EVENT_STATE_05.getCode();
                notice =ParamCode.NOTIFIED_FLAG_05.getCode();
            }else{
                eventState=ParamCode.EVENT_STATE_01.getCode();
                notice =ParamCode.NOTIFIED_FLAG_01.getCode();
            }
            //修改通知表
            //更新预警事件表
            EventEntity ev=new EventEntity();
            ev.setEventId(Integer.parseInt(eventId));
            ev.setEventState(eventState);
            ev.setHandleResults(this.getLogin(token));
            ev.setHandleTime(TimeTool.getYmdHms());
            rmEventInfoMapper.updateEventState(ev);
            //更新通知
            eventNotifiedMapper.updateNotifiedFlag(eventId,TimeTool.getYmdHms(),notice);
            
            code= ResultCode.SUCCESS.getCode();
            message="一键上报流程处理成功！流程id(唯一标识)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            message=r.getMessage();
            //throw new RuntimeException(message) ;
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="一键上报上报流程处理失败！";
            e.printStackTrace();
        }
        log.info("一键上报任务已处理，数据更新！");
        return new AppResponse(code,message);
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
            }else if(ParamCode.EVENT_STATE_05.getCode().equals(eventState)){
                throw new RuntimeException("该预警已"+ParamCode.EVENT_STATE_05.getMessage());
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
        vo.setSearchFive(login);
        return vo;

    }


    /**
     * 流程走向定义
     * @param identity
     * @param flag
     * @param userList
     * @param procInstId
     * @return
     */
    public Map<String, Object>  setFlag(String identity,String flag,List<String> userList ,String procInstId) {
        String identi = sysUserMapper.queryStarter(procInstId);
        int no=0;
        if("10000007".equals(identi) || "10000006".equals(identi)){
            no=1;
        }else{
            no=2;
        }
        int flagNo=0;
        int assiNo=0;
        if("10000007".equals(identity) || "10000006".equals(identity)){
            flagNo=no;
            assiNo=no+1;
        }else if("10000005".equals(identity) || "10000004".equals(identity)){
            flagNo=no+1;
            assiNo=no+2;
        }else if("10000003".equals(identity) || "10000002".equals(identity)){
            flagNo=no+2;
            assiNo=no+3;
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("flag"+flagNo, flag);
        if("go".equals(flag)){
            if(null!=userList && userList.size()>0){
                variables.put("handleList"+assiNo, userList);
            }else{
                throw new RuntimeException("无相关处理人，请重新确认！");
            }
        }

        return variables;
    }

    /**
     * 封装意见
     * @param map
     * @return
     */
    public String getComment(Map<String, Object> map){
        CommentEntity en =new CommentEntity();
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");
        en.setFeedBack(feedBack);
        en.setHandleResults(handleResults);
        en.setPicture(picture);
        return JsonUtils.beanToJson(en);
    }

    /**
     * 更新预警事件表/通知表
     * @param eventState
     * @param notifiedFlag
     */
    public void update(String eventId,String eventState,String notifiedFlag){

    }


    @Override
    public AppResponse getEventDp(Map<String, Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="大屏获取预警信息失败！";
        List<Map<String, Object>> mapList=null;
        Long total=0L;
        try {
            ParamsVo auth = new ParamsVo();
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
            //预警类型（all-全部；01-；02-；03-；04-）
            String eventType = (String)map.get("eventType");
            if("all".equals(eventType)){
                eventType="";
            }
            String town = (String)map.get("town");
            String venues = (String)map.get("venues");
            auth.setSearchOne(eventType);
            auth.setSearchTwo(town);
            auth.setSearchThree(venues);

            mapList = rmEventInfoMapper.getEventDp(auth);
           /* if(null!=mapList && mapList.size()>0){
                for(int i=0;i<mapList.size();i++){
                    Map<String, Object> maps = mapList.get(i);
                    String eventPicture = (String) maps.get("eventPicture");
                    if(null!=eventPicture && !eventPicture.isEmpty()){
                        List<Map<String, Object>> path = rmFileMapper.getPath(eventPicture.split(","));
                        if(null!=path && path.size()>0){
                            maps.put("eventPicture",path.get(0).get("filePath"));
                        }
                    }
                }
            }*/
            total=rmEventInfoMapper.getEventDpTotal(auth);

            code=ResultCode.SUCCESS.getCode();
            message="大屏获取预警信息成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,total,mapList.toArray());
    }

    @Override
    public AppResponse getAiSummary() {
        long code= ResultCode.FAILED.getCode();
        String message="大屏获取瓯海区预警事件汇总失败！";
        List<Map<String, Object>> mapList=new ArrayList<>();
        try {
            mapList = rmEventInfoMapper.getAiSummary();
            code=ResultCode.SUCCESS.getCode();
            message="大屏获取瓯海区预警事件汇总成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,mapList.toArray());
    }

    @Override
    public AppResponse getAiTownSummary() {
        long code= ResultCode.FAILED.getCode();
        String message="大屏获取街镇预警事件汇总失败！";
        List<Map<String, Object>> mapList=new ArrayList<>();
        try {
            mapList = rmEventInfoMapper.getAiTownSummary();
            code=ResultCode.SUCCESS.getCode();
            message="大屏获取街镇预警事件汇总成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,mapList.toArray());
    }

    @Override
    public AppResponse getMzSubmitSum(String eventState) {
        long code= ResultCode.FAILED.getCode();
        String message="推送民宗快响：数据统计失败！";
        Long sum=0l;
        try {
            sum = rmEventInfoMapper.getMzSubmitSum(eventState);
            code=ResultCode.SUCCESS.getCode();
            message="推送民宗快响：数据统计成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,sum);
    }


}
