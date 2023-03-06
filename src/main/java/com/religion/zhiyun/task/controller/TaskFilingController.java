package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskFilingService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/filing")
public class TaskFilingController {

    @Autowired
    private TaskFilingService taskFilingService;

    @RequestMapping("/launch")
    @ResponseBody
    public AppResponse launch(@RequestBody String taskJson, @RequestHeader("token")String token){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        AppResponse launch =  taskFilingService.launch(taskEntity,token);
        return launch;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public AppResponse handle(@RequestBody String taskJson,@RequestHeader("token")String token){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        AppResponse report = taskFilingService.handle(taskEntity,token);
        return report;
    }


}
