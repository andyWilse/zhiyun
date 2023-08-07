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
    //获取维修设备任务
    @GetMapping("/getRepairTask")
    public PageResponse getRepairTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return taskService.getRepairTask(map,token);
    }

    //获取维修设备详情
    @GetMapping("/getMonitorTask")
    public PageResponse getMonitorTask(@RequestParam String taskId) {
        return taskService.getMonitorTask(taskId);
    }

    //折线图（总图）
    @RequestMapping("/zxt/getTaskZxt")
    public AppResponse getZxt(@RequestParam String dateType,@RequestHeader("token")String token) {
        return taskService.getTaskZxt(-10,dateType,token);
    }

    //折线图（总图）
    @RequestMapping("/zxt/getTaskGather")
    public AppResponse getTasksGather(@RequestHeader("token")String token) {
        return taskService.getTaskGather(-7,token);
    }

    //获取所有任务
    //@RequiresPermissions("task:tasking")
    @RequestMapping("/getTasks")
    public PageResponse getTasking(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return taskService.getTasking(map,token);
    }

    @RequestMapping("/getCommon")
    public PageResponse getCommon(@RequestParam String procInstId,@RequestHeader("token")String token) {
        return taskService.getTaskCommon(procInstId,token);
    }

    //APP我的任务
    @RequestMapping("/app/getMyTask")
    public PageResponse getMyTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return taskService.getMyTask(map,token);
    }

    //APP我的任务
    @RequestMapping("/app/getFirstTask")
    public PageResponse getFirstTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return taskService.getFirstTask(map,token);
    }

    //PC任务
    @RequestMapping("/pc/getMyTask")
    public PageResponse getPcTask(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return taskService.getPcTask(map,token);
    }

    @RequestMapping("/app/getTaskDetail")
    public PageResponse getTaskDetail(@RequestParam String procInstId,@RequestHeader("token")String token) {
        return taskService.getTaskDetail(procInstId,token);
    }
}
