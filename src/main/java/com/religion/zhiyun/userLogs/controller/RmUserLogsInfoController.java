package com.religion.zhiyun.userLogs.controller;


import com.religion.zhiyun.userLogs.entity.LogsEntity;
import com.religion.zhiyun.userLogs.service.RmUserLogsInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
@CrossOrigin
public class RmUserLogsInfoController {
    @Autowired
    private RmUserLogsInfoService rmUserLogsInfoService;

    @PostMapping("/addLogs")
    @ResponseBody
    public String addLogs(@RequestBody String logsJson) {
        LogsEntity logsEntity = JsonUtils.jsonTOBean(logsJson, LogsEntity.class);
        rmUserLogsInfoService.addlogs(logsEntity);
        return "添加成功！";
    }

    @PostMapping("/deleteLogs")
    public void deleteLogs() {
        rmUserLogsInfoService.deletelogs("logsId");
    }

    @RequestMapping("/allLogs")
    public String allLogs() {
        List<LogsEntity> list = rmUserLogsInfoService.alllogs();
        return JsonUtils.objectTOJSONString(list);
    }

    @PostMapping("/updateLogs")
    public void updateLogs(@RequestBody String logsJson) {
        LogsEntity logsEntity=JsonUtils.jsonTOBean(logsJson,LogsEntity.class);
        rmUserLogsInfoService.updatelogs(logsEntity);
    }

    @GetMapping("/findpage")
    public RespPageBean getLogsByPage(@RequestParam Map<String, Object> map){

        String userName = (String)map.get("userName");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");

        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return rmUserLogsInfoService.getLogsByPage(page,size,userName);
    }


}
