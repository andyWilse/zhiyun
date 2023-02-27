package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskReportService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import lombok.extern.slf4j.Slf4j;
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
    public Object launch(@RequestBody String taskJson){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        Object launch = warnTaskService.launch(taskEntity);
        return "节点执行人："+launch;
    }

    @RequestMapping("/report")
    @ResponseBody
    public AppResponse report(@RequestParam Map<String, Object> map){
        String procInstId = (String)map.get("procInstId");
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");

        AppResponse report = warnTaskService.report(procInstId, handleResults, feedBack, picture);
        return report;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public AppResponse handle(@RequestParam Map<String, Object> map){
        String procInstId = (String)map.get("procInstId");
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");

        AppResponse handle = warnTaskService.handle(procInstId, handleResults, feedBack, picture);
        return handle;
    }

    @RequestMapping("/getTasking")
    public RespPageBean getTasking(@RequestParam Map<String, Object> map) {
        String taskName = (String) map.get("taskName");
        String taskContent = (String) map.get("taskContent");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");

        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return warnTaskService.getTasking(page,size,taskName,taskContent);
    }

    @RequestMapping("/getTaskStatistics")
    public AppResponse getTaskStatistics() {
        return warnTaskService.getTaskNum();
    }

    @RequestMapping("/unFinish")
    @ResponseBody
    public AppResponse getUnTask(@RequestParam Map<String, Object> map){
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return warnTaskService.getUnTask(page,size);
    }

    @RequestMapping("/finish")
    @ResponseBody
    public AppResponse getFinishTask(@RequestParam Map<String, Object> map){
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return warnTaskService.getFinishTask(page,size);
    }

    @RequestMapping("/que")
    @ResponseBody
    public Object queryTasks(){
        return warnTaskService.queryTasks();
    }

}
