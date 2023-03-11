package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import org.springframework.web.bind.annotation.RequestHeader;

public interface TaskFilingService {
    /** 提交申请(发起) **/
    public AppResponse launch(TaskEntity taskEntity, String token);

    /** 提交申请(处理) **/
    public AppResponse handle(TaskEntity taskEntity,String token);

    /** 获取历史备案信息 **/
    public PageResponse getFillHistory(Integer page,Integer size,String search, String token);

    /** 获取历史备案详情 **/
    public PageResponse getFillHisDetail(String filingId);
}
