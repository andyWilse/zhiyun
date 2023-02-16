package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskFilingService;
import com.religion.zhiyun.task.service.TaskIssuedService;
import com.religion.zhiyun.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/filing")
public class TaskFilingController {

    @Autowired
    private TaskFilingService taskFilingService;

    @RequestMapping("/launch")
    @ResponseBody
    public String launch(@RequestBody String taskJson){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        String launch = (String) taskFilingService.launch(taskEntity);
        return "节点执行人："+launch;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public String handle(@RequestBody String taskJson){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        String report = (String) taskFilingService.handle(taskEntity);
        return report;
    }
}
