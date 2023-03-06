package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import org.springframework.web.bind.annotation.RequestHeader;

public interface TaskFilingService {
    /** 提交申请(发起) **/
    public AppResponse launch(TaskEntity taskEntity, String token);

    /** 提交申请(处理) **/
    public AppResponse handle(TaskEntity taskEntity,String token);
}
