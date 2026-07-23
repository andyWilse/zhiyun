package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskAiWarnService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/aiWarnTask")
public class TaskAiWarnController {

    @Autowired
    private TaskAiWarnService aiWarnService;

    //发起流程
    @RequestMapping("/launch")
    @ResponseBody
    public AppResponse launchTask(@RequestBody String taskJson, @RequestHeader("token")String token){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        AppResponse launch = aiWarnService.launch(taskEntity,null,token);
        return launch;
    }

   //人工审核
    @RequestMapping("/review")
    @ResponseBody
    public AppResponse reviewTask(@RequestParam Map<String, Object> map, @RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String review = (String)map.get("review");
        AppResponse report = aiWarnService.review(review,procInstId,token);
        return report;
    }

    //基层干部
    @RequestMapping("/handle")
    @ResponseBody
    public AppResponse handleTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");

        AppResponse handle = aiWarnService.handle(procInstId, handleResults, feedBack, picture,token);
        return handle;
    }

    //评价通过
    @RequestMapping("/evaluate")
    @ResponseBody
    public AppResponse evaluateTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String evaluation = (String)map.get("evaluation");
        AppResponse handle = aiWarnService.evaluate(procInstId, evaluation,token);
        return handle;
    }
    //终审退回基层处置岗
    @RequestMapping("/backup")
    @ResponseBody
    public AppResponse goBack(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String evaluation = (String)map.get("evaluation");
        AppResponse handle = aiWarnService.backup(procInstId, evaluation,token);
        return handle;
    }

    //解除误报
    @RequestMapping("/dismiss")
    @ResponseBody
    public AppResponse dismissAi(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        String procInstId = (String)map.get("procInstId");
        return aiWarnService.dismissAI(procInstId,token);
    }

}
