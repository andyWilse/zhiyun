package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.response.AppResponse;

public interface TaskIssuedService {
    /** 流程部署。部署一次就可以了。**/
    public AppResponse deployment();

    /** 提交申请(发起) **/
    public AppResponse launch(String taskJson,String token);

    /** 提交申请(处理) **/
    public AppResponse handle(String procInstId, String handleResults, String feedBack, String picture,String token);
}
