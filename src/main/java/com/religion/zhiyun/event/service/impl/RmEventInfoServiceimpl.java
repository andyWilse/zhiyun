package com.religion.zhiyun.event.service.impl;

import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.sys.login.api.ResultCode;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class RmEventInfoServiceimpl implements RmEventInfoService {
    @Autowired
    private RmEventInfoMapper rmEventInfoMapper;

    @Autowired
    private RmMonitroInfoMapper rmMonitroInfoMapper;

    @Override
    public void addEvent(String eventJson) {
        Map<String, Object> map = JsonUtils.jsonToMap(eventJson);
        String videoUrl= (String) map.get("videoUrl");
        String cameraId= (String) map.get("cameraId");
        String locationName= (String) map.get("locationName");
        String timeStamp= (String) map.get("timeStamp");

        String alarmCode= (String) map.get("alarmCode");
        String alarmLevelName= (String) map.get("alarmLevelName");
        String infoSource= (String) map.get("infoSource");

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
        rmEventInfoMapper.addEvent(event);
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
    public RespPageBean getByType(String eventType) {
        long code= ResultCode.SUCCESS.getCode();
        List<Map<String, Object>> map=null;
        try {
            if("all".equals(eventType)){
                eventType="";
            }
            map = rmEventInfoMapper.getByType(eventType);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }

        return new RespPageBean(code,map);
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
    public RespPageBean getEvents() {
        long code= ResultCode.SUCCESS.getCode();
        List<Map<String, Object>> events = null;
        try {
            events = rmEventInfoMapper.getEvents(ParamCode.EVENT_STATE_00.getCode(),"");
            for(int i=0;i<events.size();i++){
                Map<String, Object> map = events.get(0);
                String path = (String) map.get("path");
                String[] split = path.split(",");
                map.put("path",split[0]);
            }
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }

        return new RespPageBean(code,events);
    }

    @Override
    public void addEventByNB(String eventEntity) {
        Map<String, Object> map = JsonUtils.jsonToMap(eventEntity);
        EventEntity event=new EventEntity();
        String deviceType= (String) map.get("deviceType");//设备类型
        String deviceId=(String) map.get("deviceId");
        String deviceName= (String) map.get("deviceName");//设备名字
        String at= (String) map.get("at");//事发时间
        String type= (String) map.get("type");//事件类型   "alarm": 告警；"clean": 告警清除；"reset": 设备重置；"dat": 数据上报
        String data= (String) map.get("data");//消息内容
        String level= (String) map.get("level");//消息  事件=0,次要=1,重要=2,严重=3, 不填为严重。（火警默认严重
        String location= (String) map.get("location");//位置
        String rawData= (String) map.get("rawData");//原始数据

        event.setDeviceName(deviceName);
        event.setAccessNumber(deviceId);
        event.setDeviceType(deviceType);
        event.setWarnTime(at);
        event.setEventType("01");//默认传来的都是火警
        event.setRawData(rawData);
        event.setEventData(data);
        event.setEventLevel(level);
        event.setLocation(location);
        event.setEventState("00");
        event.setHandleTesults("待处理");

        Timestamp timestamp = new Timestamp(new Date().getTime());
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        event.setHandleTime(format);
        rmEventInfoMapper.addEventByNB(event);
    }





}
