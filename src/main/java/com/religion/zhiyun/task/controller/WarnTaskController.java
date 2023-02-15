package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.WarnTaskService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.RespPageBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public Object launch(@RequestBody String taskJson){
        TaskEntity taskEntity = JsonUtils.jsonTOBean(taskJson, TaskEntity.class);
        Object launch = warnTaskService.launch(taskEntity);
        return "节点执行人："+launch;
    }

    @RequestMapping("/report")
    @ResponseBody
    public Object report(@RequestParam String procInstId){
        Object report = warnTaskService.report(procInstId);
        return report;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public Object handle(@RequestParam String procInstId,@RequestParam String handleResults){
        Object approve = warnTaskService.handle(procInstId, handleResults);
        return approve;
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
    public String getTaskStatistics() {
        return warnTaskService.getTaskNum();
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

    @RequestMapping("/que")
    @ResponseBody
    public Object queryTasks(){
        return warnTaskService.queryTasks();
    }

}
