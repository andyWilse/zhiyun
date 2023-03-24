package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.task.service.TaskIssuedService;
import com.religion.zhiyun.utils.response.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public AppResponse launch(@RequestBody String taskJson, @RequestHeader("token")String token){

        AppResponse launch = taskIssuedService.launch(taskJson,token);
        return launch;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public AppResponse handle(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");

        AppResponse report = taskIssuedService.handle(procInstId,handleResults,feedBack,picture,token);
        return report;
    }

}
