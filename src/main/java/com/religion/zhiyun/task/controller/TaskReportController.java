package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskReportService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/eventTask")
public class TaskReportController {

    @Autowired
    private TaskReportService warnTaskService;


    @RequestMapping("/deploy")
    @ResponseBody
    public Object deployment(){
        Object id = warnTaskService.deployment();
        return "流程部署成功，流程id："+id;
    }

    @RequestMapping("/launch")
    @ResponseBody
    public AppResponse launch(@RequestBody String taskJson,@RequestHeader("token")String token){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        AppResponse launch = warnTaskService.launch(taskEntity,token);
        return launch;
    }

    @RequestMapping("/report")
    @ResponseBody
    public AppResponse report(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");

        AppResponse report = warnTaskService.report(procInstId, handleResults, feedBack, picture,token);
        return report;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public AppResponse handle(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String procInstId = (String)map.get("procInstId");
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");

        AppResponse handle = warnTaskService.handle(procInstId, handleResults, feedBack, picture,token);
        return handle;
    }

    @RequestMapping("/getTaskStatistics")
    public AppResponse getTaskStatistics(@RequestHeader("token")String token) {
        return warnTaskService.getTaskNum(token);
    }

    @RequestMapping("/unFinish")
    @ResponseBody
    public AppResponse getUnTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return warnTaskService.getUnTask(page,size,token);
    }

    @RequestMapping("/finish")
    @ResponseBody
    public AppResponse getFinishTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return warnTaskService.getFinishTask(page,size,token);
    }

    @RequestMapping("/que")
    @ResponseBody
    public Object queryTasks(){
        return warnTaskService.queryTasks();
    }

}
