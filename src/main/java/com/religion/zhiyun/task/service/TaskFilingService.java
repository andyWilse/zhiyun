package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.entity.UpFillEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

public interface TaskFilingService {
    /** 提交申请(发起) **/
    public AppResponse launch(String taskJson, String token);

    /** 提交申请(处理) **/
    public AppResponse handle(Map<String,Object> map, String token);

    /** 获取历史备案信息 **/
    public PageResponse getFillHistory(Integer page,Integer size,String search, String token);

    /** 获取历史备案详情 **/
    public PageResponse getFillHisDetail(String filingId);
}
