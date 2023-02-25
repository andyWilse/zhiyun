package com.religion.zhiyun.event.controller;

import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.OutInterfaceResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/event")
public class RmEventInfoController {
    @Autowired
    private RmEventInfoService rmEventInfoService;

    @PostMapping("/addEvent")
    @ResponseBody
    public OutInterfaceResponse addEvent(@RequestBody String eventJson) {
        return rmEventInfoService.addEvent(eventJson);
    }

    @PostMapping("/updateEvent")
    public void updateEvent(@RequestBody String eventJson) {
        EventEntity eventEntity=JsonUtils.jsonTOBean(eventJson,EventEntity.class);
        rmEventInfoService.updateEvent(eventEntity);
    }

    @PostMapping("/deleteEvent/{eventId}")
    public void delete(@PathVariable int eventId) {
        rmEventInfoService.deleteEvent(eventId);
    }


    @RequestMapping(value = "/allEvent",method = RequestMethod.POST)
    public String allEvent() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*"); //设置访问源请求头
        List<EventEntity> list = rmEventInfoService.allEvent();
        return JsonUtils.objectTOJSONString(list);
    }

    /**
     * 未完成的事件展示
     * @return
     */
    @RequestMapping(value = "/undoEvents")
    public RespPageBean allEventBytime() {
        //List<EventEntity> list = rmEventInfoService.allEventByState();
        return rmEventInfoService.getEvents();
    }

    /**
     * 根据事件id查询事件、场所、监控设备信息
     * @param eventId
     * @return
     */
    @RequestMapping("/getByEventId")//错误
    public String getByEventId(String eventId) {
        EventEntity byEventId = rmEventInfoService.getByEventId(eventId);
        return JsonUtils.objectTOJSONString(byEventId);
    }

    /**
     * 根据类型分类   （全部，人脸，火警。任务。人流）
      * @param map
     * @return
     */
    @RequestMapping("/getEventByType")
    public AppResponse getEventByType(@RequestParam Map<String, Object> map) {
        String eventType = (String)map.get("eventType");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);
        return rmEventInfoService.getByType(page,size,eventType);
    }

    /**
     * 事件类型的数量
     * @return
     */
    @RequestMapping("/getAllNum")
    public String getAllNum(){
        List<Map<String, Object>> list = rmEventInfoService.getAllNum();
        return JsonUtils.objectTOJSONString(list);
    }

    @GetMapping("/findpage")
    public RespPageBean getEventByPage(@RequestParam Map<String, Object> map){

        String accessNumber = (String)map.get("accessNumber");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);
        return rmEventInfoService.getEventsByPage(page,size,accessNumber);
    }


    /**
     * 接受NB烟感器的数据转化为实体
     * @param eventJson
     * @return
     */
    @PostMapping("/addEventByNB")
    @ResponseBody
    public OutInterfaceResponse addEventByNB(@RequestBody String eventJson) {
        return rmEventInfoService.addEventByNB(eventJson);
    }

    //根据登录人不同，通知不同
    @RequestMapping("/zlb/getEventsByState")
    public AppResponse getEventsByState(@RequestParam Map<String, Object> map) {
        String eventState = (String)map.get("eventState");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);
        return rmEventInfoService.getEventsByState(page,size,eventState);
    }

    //解除误报
    @RequestMapping("/zlb/dismiss")
    public AppResponse dismissEvents(@RequestParam Map<String, Object> map) {
        String eventId = (String)map.get("eventId");
        return rmEventInfoService.dismissEvent(eventId);
    }

    //拨打119
    @RequestMapping("/zlb/callFire")
    public AppResponse callFire(@RequestParam Map<String, Object> map) {
        String eventId = (String)map.get("eventId");
        return rmEventInfoService.callFire(eventId);
    }

    //一键上报
    @RequestMapping("/zlb/reportOne")
    public AppResponse reportOne(@RequestParam Map<String, Object> map) {
        String eventId = (String)map.get("eventId");
        return rmEventInfoService.reportOne(eventId);
    }

    //折线图（月）
    @RequestMapping("/zxt/month")
    public AppResponse getEventsMonth(@RequestParam String eventType) {
        return rmEventInfoService.getEventsMonth(-9,eventType);
    }

    //折线图（日）
    @RequestMapping("/zxt/day")
    public AppResponse getEventsDay(@RequestParam String eventType) {
        return rmEventInfoService.getEventsDay(-10,eventType);
    }

    //折线图（周）
    @RequestMapping("/zxt/week")
    public AppResponse getEventsWeek(@RequestParam String eventType) {
        return rmEventInfoService.getEventsWeek(-10,7*(-9)-1,eventType);
    }

    //折线图（总图）
    @RequestMapping("/zxt/eventGather")
    public AppResponse getEventsGather(@RequestParam String dateType) {
        return rmEventInfoService.getEventsGather(-10,dateType);
    }




    }
