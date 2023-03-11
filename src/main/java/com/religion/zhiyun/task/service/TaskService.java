package com.religion.zhiyun.task.service;

import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

public interface TaskService {
    /** 流程部署。部署一次就可以了。**/
    public AppResponse deployment(String taskKey);

    /*** 获取流程部署信息  **/
    RespPageBean getProcdef(Integer page,Integer size,String taskName);

    /*** 获取流程部署信息  **/
    PageResponse getMonitorTask(String taskId);
}
