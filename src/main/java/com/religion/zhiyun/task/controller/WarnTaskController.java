package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.task.service.WarnTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/eventTask")
public class WarnTaskController {

    @Autowired
    private WarnTaskService warnTaskService;


    @RequestMapping("/deploy")
    @ResponseBody
    public Object deployment(){
        Object id = warnTaskService.deployment();
        return "流程部署成功，流程id："+id;
    }

    @RequestMapping("/launch")
    @ResponseBody
    public Object launch(){
        warnTaskService.launch();
        return null;
    }

    @RequestMapping("/report")
    @ResponseBody
    public Object report(){
        warnTaskService.report();
        return null;
    }

    @RequestMapping("/approve")
    @ResponseBody
    public Object approve(){
        warnTaskService.approve();
        return null;
    }

    @RequestMapping("/unFinish")
    @ResponseBody
    public Object getUnTask(){
        return warnTaskService.getUnTask();
    }

    @RequestMapping("/finish")
    @ResponseBody
    public Object getFinishTask(){
        return warnTaskService.getFinishTask();
    }

}
