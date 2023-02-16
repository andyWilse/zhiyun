package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;

public interface TaskFilingService {
    /** 提交申请(发起) **/
    public Object launch(TaskEntity taskEntity);

    /** 提交申请(处理) **/
    public Object handle(TaskEntity taskEntity);
}
