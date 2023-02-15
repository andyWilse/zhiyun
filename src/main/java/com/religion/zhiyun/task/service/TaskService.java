package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.ProcdefEntity;
import com.religion.zhiyun.utils.RespPageBean;

import java.util.List;

public interface TaskService {
    /** 流程部署。部署一次就可以了。**/
    public String deployment(String taskKey);

    /*** 获取流程部署信息  **/
    RespPageBean getProcdef(Integer page,Integer size,String taskName);
}
