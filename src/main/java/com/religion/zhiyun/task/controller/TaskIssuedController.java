package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskIssuedService;
import com.religion.zhiyun.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issued")
public class TaskIssuedController {
    @Autowired
    private TaskIssuedService taskIssuedService;

    @RequestMapping("/deploy")
    @ResponseBody
    public Object deployment(){
        Object id = taskIssuedService.deployment();
        return "流程部署成功，流程id："+id;
    }

    @RequestMapping("/launch")
    @ResponseBody
    public Object launch(@RequestBody String taskJson){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        Object launch = taskIssuedService.launch(taskEntity);
        return "节点执行人："+launch;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public Object handle(@RequestParam String procInstId){
        Object report = taskIssuedService.handle(procInstId);
        return report;
    }

}
