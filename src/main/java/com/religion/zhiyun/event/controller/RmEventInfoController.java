package com.religion.zhiyun.event.controller;

import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.service.RmEventInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
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

    @RequestMapping("/allEvent")
    public String allEvent() {
        List<EventEntity> list = rmEventInfoService.allEvent();
        return JsonUtils.objectTOJSONString(list);
    }

}
