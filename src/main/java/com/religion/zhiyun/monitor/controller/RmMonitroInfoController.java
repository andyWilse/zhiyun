package com.religion.zhiyun.monitor.controller;


import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.monitor.service.RmMonitroInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitor")
public class RmMonitroInfoController {
    @Autowired
    private RmMonitroInfoService rmMonitroInfoService;

    @RequestMapping("/allMonitor")
    public String allMonitor(){
        List<MonitroEntity> list = rmMonitroInfoService.allMonitro();
        return JsonUtils.objectTOJSONString(list);
    }

    @ResponseBody
    @PostMapping("/addMonitro")
    public String addMonitro(@RequestBody String monitroJson){
        MonitroEntity monitroEntity = JsonUtils.jsonTOBean(monitroJson, MonitroEntity.class);
        rmMonitroInfoService.addMonitro(monitroEntity);
        return "添加成功！";
    }

    @PostMapping("/updateMonitro")
    public void updateMonitro(@RequestBody String monitroJson){
        MonitroEntity monitroEntity = JsonUtils.jsonTOBean(monitroJson, MonitroEntity.class);
        rmMonitroInfoService.updateMonitro(monitroEntity);
    }

    @PostMapping("/deleteMonitro")
    public void deleteMonitro(){
        rmMonitroInfoService.deleteMonitro("monitroId");
    }

}
