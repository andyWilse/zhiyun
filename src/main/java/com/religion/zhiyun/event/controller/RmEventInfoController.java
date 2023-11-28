package com.religion.zhiyun.event.controller;

import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.OutInterfaceResponse;
import com.religion.zhiyun.utils.response.PageResponse;
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

    /**
     * 接受NB烟感器的数据转化为实体
     * @param eventJson
     * @return
     */
    @PostMapping("/addEventByNB")
    @ResponseBody
    public OutInterfaceResponse addEventByNB(@RequestBody String eventJson) {
        log.info("NB烟感器的数据已接收:"+eventJson);

        new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("新线程内逻辑执行..." + System.currentTimeMillis());
                rmEventInfoService.addEventByNB(eventJson);
                log.info("执行完毕 " + System.currentTimeMillis());
            }
        }).start();

        return new OutInterfaceResponse(200,"NB烟感器的数据已接收。");

    }

    @PostMapping("/addEvent")
    @ResponseBody
    public AppResponse addAiEvent(@RequestBody String eventJson) {
        return rmEventInfoService.addAiEvent(eventJson);
    }

   /* @PostMapping("/pushAiEvent")
    @ResponseBody
    public OutInterfaceResponse addEvent(@RequestBody String eventJson) {
        return rmEventInfoService.addEvent(eventJson);
    }*/

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
    public RespPageBean getUndoEvents(@RequestParam Map<String,Object> map,@RequestHeader("token")String token) {
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return rmEventInfoService.getUndoEvents(page,size,token);
    }

    /**
     * 未完成的事件详情展示
     * @return
     */
    @RequestMapping(value = "/getEventDetail")
    public PageResponse getUndoEventDetail(@RequestParam("eventId")String eventId) {
        return rmEventInfoService.getUndoEventDetail(eventId);
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
    public AppResponse getEventByType(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return rmEventInfoService.getByType(map,token);
    }

    /**
     * 事件类型的数量
     * @return
     */
    @RequestMapping("/getAllNum")
    public AppResponse getAllNum(@RequestParam String type,@RequestHeader("token")String token){
        return rmEventInfoService.getAllNum(type,token);
    }

    @GetMapping("/findPage")
    public RespPageBean getEventByPage(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        return rmEventInfoService.getEventsByPage(map,token);
    }

    //根据登录人不同，通知不同
    @RequestMapping("/zlb/getEventsByState")
    public AppResponse getEventsByState(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        String eventState = (String)map.get("eventState");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);
        return rmEventInfoService.getEventsByState(page,size,eventState,token);
    }

    //一键上报（处理）
    @RequestMapping("/reportOne/handle")
    @ResponseBody
    public AppResponse reportOneHandle(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        AppResponse report = rmEventInfoService.reportOneHandle(map,token);
        return report;
    }

    //一键上报（上报）
    @RequestMapping("/reportOne/report")
    @ResponseBody
    public AppResponse reportOneReport(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        AppResponse report = rmEventInfoService.reportOneReport(map,token);
        return report;
    }
    //解除误报
    @RequestMapping("/zlb/dismiss")
    public AppResponse dismissEvents(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        String eventId = (String)map.get("eventId");
        return rmEventInfoService.dismissEvent(eventId,token);
    }

    //拨打119
    @RequestMapping("/zlb/callFire")
    public AppResponse callFire(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        String eventId = (String)map.get("eventId");
        return rmEventInfoService.callFire(eventId,token);
    }

    //一键上报（管理）
    @RequestMapping("/reportOne/notice")
    public AppResponse reportOne(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        String eventId = (String)map.get("eventId");
        //String procInstId = (String)map.get("procInstId");
        return rmEventInfoService.reportOne(eventId,token);
    }

    //折线图（月）
    @RequestMapping("/zxt/month")
    public AppResponse getEventsMonth(@RequestParam String eventType,@RequestHeader("token")String token) {
        return rmEventInfoService.getEventsMonth(-9,eventType,token);
    }

    //折线图（日）
    @RequestMapping("/zxt/day")
    public AppResponse getEventsDay(@RequestParam String eventType,@RequestHeader("token")String token) {
        return rmEventInfoService.getEventsDay(-10,eventType,token);
    }

    //折线图（周）
    @RequestMapping("/zxt/week")
    public AppResponse getEventsWeek(@RequestParam String eventType,@RequestHeader("token")String token) {
        return rmEventInfoService.getEventsWeek(-10,7*(-9)-1,eventType,token);
    }

    //折线图（总图）
    @RequestMapping("/zxt/eventGather")
    public AppResponse getEventsGather(@RequestParam String dateType,@RequestHeader("token")String token) {
        return rmEventInfoService.getEventsGather(-10,dateType,token);
    }

    //折线图（动态）
    @RequestMapping("/zxt/trends")
    public AppResponse getEventsTrends(@RequestHeader("token")String token) {
        return rmEventInfoService.getEventsTrends(-8,token);
    }

    //预警状态查询
    @RequestMapping("/alertEvent")
    public AppResponse alertEvent(@RequestParam String eventId,@RequestHeader("token")String token) {
        return rmEventInfoService.alertEvent(eventId,token);
    }


    //折线图（日）
    @GetMapping("/daPing/day")
    public AppResponse getDaPingDay(@RequestParam String eventType) {
        return rmEventInfoService.getEventsDay(-5,eventType);
    }

    //折线图（总图）
    @RequestMapping("/daPing/gather")
    public AppResponse getDaPingGather(@RequestParam String dateType) {
        return rmEventInfoService.getDaPingGather(-8,dateType);
    }

    @GetMapping("/getEventDp")
    public AppResponse getEventDp(@RequestParam Map<String, Object> map) {
        return rmEventInfoService.getEventDp(map);
    }

    @GetMapping("/getAiSummary")
    public AppResponse getAiSummary() {
        return rmEventInfoService.getAiSummary();
    }

    @GetMapping("/getAiTownSummary")
    public AppResponse getAiTownSummary() {
        return rmEventInfoService.getAiTownSummary();
    }

}
