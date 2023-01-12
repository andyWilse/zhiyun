package com.religion.zhiyun.monitor.controller;


import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.monitor.service.RmMonitroInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monitor")
@CrossOrigin(origins = {"*"})
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

    //摄像头数量
    @RequestMapping("/getAllNum")
    public String getAllNum(){
        List<Map<String, Object>> list = rmMonitroInfoService.getAllNum();
        return JsonUtils.objectTOJSONString(list);
    }

    /**
     * 根据摄像头状态查看摄像头信息
     * @param state
     * @return
     */
    @RequestMapping("/getMonitorByState")
    public String getMonitorByState(String state){
        List<MonitroEntity> list = rmMonitroInfoService.getMonitorByState(state);
        return JsonUtils.objectTOJSONString(list);
    }

    @RequestMapping("/getMonitorByVenuesId")//有问题
    public String getMonitorByVenuesId(String state){
        List<MonitroEntity> list = rmMonitroInfoService.getMonitorByVenuesId(state);
        return JsonUtils.objectTOJSONString(list);
    }
}
