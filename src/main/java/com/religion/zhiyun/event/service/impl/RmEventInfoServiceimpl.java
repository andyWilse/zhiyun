package com.religion.zhiyun.event.service.impl;

import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.sys.login.api.ResultCode;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.OutInterfaceResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RmEventInfoServiceimpl implements RmEventInfoService {
    @Autowired
    private RmEventInfoMapper rmEventInfoMapper;

    @Autowired
    private RmMonitroInfoMapper rmMonitroInfoMapper;

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
    public OutInterfaceResponse addEventByNB(String eventEntity) {
        System.out.println("接受NB烟感器的数据:"+eventEntity);
        //log.info("接受NB烟感器的数据:"+eventEntity);
        long code=0l;
        String message="";
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

            code=ResultCode.SUCCESS.getCode();
            message="NB烟感器数据处理成功！";
        }catch (Exception e){
            code=ResultCode.FAILED.getCode();
            message="NB烟感器数据处理失败！";
            e.printStackTrace();
        }

        return new OutInterfaceResponse(code,message);
    }





}
