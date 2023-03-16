package com.religion.zhiyun.task.controller;

import com.alibaba.fastjson.JSONObject;
import com.religion.zhiyun.task.service.TaskService;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @ResponseBody
    @RequestMapping("/deploy")
    public AppResponse deployment(@RequestBody JSONObject jsonObject){
        String taskKey = jsonObject.getString("taskKey");
        return taskService.deployment(taskKey);
    }

    @ResponseBody
    @RequestMapping("/getProcdef")
    public RespPageBean getProcdef(@RequestParam Map<String, Object> map){
        String taskName = (String)map.get("taskName");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);
        return taskService.getProcdef(page,size,taskName);
    }

    //获取维修设备详情
    @GetMapping("/getMonitorTask")
    public PageResponse getMonitorTask(@RequestParam String taskId) {
        return taskService.getMonitorTask(taskId);
    }

    //折线图（总图）
    @RequestMapping("/zxt/tasksGather")
    public AppResponse getTasksGather(@RequestParam String dateType) {
        return taskService.getTasksGather(-10,dateType);
    }
}
