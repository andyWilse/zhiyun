package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.Map;

public interface TaskService {
    /** 流程部署。部署一次就可以了。**/
    public AppResponse deployment(String taskKey);

    /*** 获取流程部署信息  **/
    RespPageBean getProcdef(Integer page,Integer size,String taskName);

    /*** 获取流程部署信息  **/
    PageResponse getMonitorTask(String taskId);

    /** 提交申请(上报) **/
    public AppResponse reportOneReport(Map<String, Object> map, String token);

    /** 流程结束 **/
    public AppResponse reportOneHandle(Map<String, Object> map,String token);

    /** 获取未完成任务 **/
    public PageResponse getTasking(Map<String, Object> map,String token);

    /*** 获取流程流转意见 **/
    PageResponse getTaskCommon(String procInstId);

}
