package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.List;
import java.util.Map;

public interface TaskReportService {
    /** 流程部署。部署一次就可以了。**/
    public Object deployment();

    /** 提交申请(发起) **/
    public Object launch(TaskEntity taskEntity);

    /** 提交申请(上报) **/
    public AppResponse report(String procInstId, String handleResults, String feedBack, String picture);

    /** 流程结束 **/
    public AppResponse handle(String procInstId, String handleResults, String feedBack, String picture);

    /** 获取未完成任务 **/
    public RespPageBean getTasking(Integer page, Integer size,String taskName, String taskContent);

    /** 统计任务数量 **/
    public AppResponse getTaskNum();

    /** 查询用户未完成的历史记录**/
    public AppResponse getUnTask();

    /** 查询完成的历史记录 **/
    public AppResponse getFinishTask();

    /**查询任务**/
    List<Map<String,Object>> queryTasks();
}
