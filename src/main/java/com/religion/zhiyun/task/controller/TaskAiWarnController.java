package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.login.http.inter.DecryptRequest;
import com.religion.zhiyun.login.http.inter.EncryptResponse;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskAiWarnService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@DecryptRequest(true)
@EncryptResponse(true)
@RestController
@RequestMapping("/aiWarnTask")
public class TaskAiWarnController {

    @Autowired
    private TaskAiWarnService aiWarnService;

    //发起流程
    @DecryptRequest(false)
    @EncryptResponse(false)
    @PostMapping("/launch")
    public AppResponse launchTask(@RequestBody String taskJson, @RequestHeader("token")String token){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        AppResponse launch = aiWarnService.launch(taskEntity,null,token);
        return launch;
    }

   //人工审核
   @DecryptRequest(false)
   @EncryptResponse(false)
    @PostMapping("/review")
    public AppResponse reviewTask(@RequestParam Map<String, Object> map, @RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String review = (String)map.get("review");
        AppResponse report = aiWarnService.review(review,procInstId,token);
        return report;
    }

    //基层干部
    @DecryptRequest(false)
    @EncryptResponse(false)
    @PostMapping("/handle")
    public AppResponse handleTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");

        AppResponse handle = aiWarnService.handle(procInstId, handleResults, feedBack, picture,token);
        return handle;
    }

    //评价通过
    @DecryptRequest(false)
    @EncryptResponse(false)
    @PostMapping("/evaluate")
    public AppResponse evaluateTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String evaluation = (String)map.get("evaluation");
        AppResponse handle = aiWarnService.evaluate(procInstId, evaluation,token);
        return handle;
    }
    //终审退回基层处置岗
    @DecryptRequest(false)
    @EncryptResponse(false)
    @PostMapping("/backup")
    public AppResponse goBack(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String evaluation = (String)map.get("evaluation");
        AppResponse handle = aiWarnService.backup(procInstId, evaluation,token);
        return handle;
    }

    //解除误报
    @DecryptRequest(false)
    @EncryptResponse(false)
    @PostMapping("/dismiss")
    public AppResponse dismissAi(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        String procInstId = (String)map.get("procInstId");
        return aiWarnService.dismissAI(procInstId,token);
    }

    //获取所有流程节点
    @GetMapping("/getAct")
    public AppResponse getAiTaskAct(@RequestParam("procInstId") String procInstId) {
        return aiWarnService.getAiTaskAct(procInstId);
    }

    //删除流程节点
    @PostMapping("/delAct/{actId}")
    public AppResponse deleteTaskAct(@PathVariable int actId,@RequestHeader("token")String token) {
        return aiWarnService.deleteTaskAct(actId,token);
    }

    //获取单个流程节点
    @GetMapping("/getNode/{actId}")
    public PageResponse getTaskAct(@PathVariable int actId) {
        return aiWarnService.getTaskActDetail(actId);
    }

    //流程修改保存
    @PostMapping("/saveAct")
    public AppResponse saveTaskAct(@RequestBody Map<String, Object> map,@RequestHeader("token")String token) {
        return aiWarnService.saveTaskAct(map,token);
    }

    //删除任务接收人
    @PostMapping("/delAss/{assId}")
    public AppResponse deleteTaskAss(@PathVariable int assId,@RequestHeader("token")String token) {
        return aiWarnService.deleteTaskAss(assId,token);
    }

    //流程修改保存
    @PostMapping("/saveAss")
    public AppResponse saveTaskAss(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return aiWarnService.saveTaskAss(map,token);
    }

}
