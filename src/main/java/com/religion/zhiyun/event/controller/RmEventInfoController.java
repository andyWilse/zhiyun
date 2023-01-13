package com.religion.zhiyun.event.controller;

import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/event")
@CrossOrigin(origins = {"*"})
public class RmEventInfoController {
    @Autowired
    private RmEventInfoService rmEventInfoService;

    @PostMapping("/addEvent")
    @ResponseBody
    public String addEvent(@RequestBody String eventJson) {
        EventEntity eventEntity = JsonUtils.jsonTOBean(eventJson, EventEntity.class);
        rmEventInfoService.addEvent(eventEntity);
        return "添加成功！";
    }

    @PostMapping("/updateEvent")
    public void updateEvent(@RequestBody String eventJson) {
        EventEntity eventEntity=JsonUtils.jsonTOBean(eventJson,EventEntity.class);
        rmEventInfoService.updateEvent(eventEntity);
    }

    @PostMapping("/deleteEvent")
    public void delete() {
        rmEventInfoService.deleteEvent("eventId");
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
    @RequestMapping(value = "/allEventBytime",method = RequestMethod.POST)
    public String allEventBytime() {
        List<EventEntity> list = rmEventInfoService.allEventByState();
        return JsonUtils.objectTOJSONString(list);
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
      * @param eventType
     * @return
     */
    @RequestMapping("/getEventByType")
    public String getEventByType( String eventType) {
        List<EventEntity> list = rmEventInfoService.getByType(eventType);
        return JsonUtils.objectTOJSONString(list);
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

}
