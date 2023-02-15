package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;

public interface TaskIssuedService {
    /** 流程部署。部署一次就可以了。**/
    public Object deployment();

    /** 提交申请(发起) **/
    public Object launch(TaskEntity taskEntity);

    /** 提交申请(处理) **/
    public Object handle(String procInstId);
}
