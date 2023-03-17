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
    public AppResponse launch(TaskEntity taskEntity,String token);

    /** 提交申请(上报) **/
    public AppResponse report(String procInstId, String handleResults, String feedBack, String picture,String token);

    /** 流程结束 **/
    public AppResponse handle(String procInstId, String handleResults, String feedBack, String picture,String token);

    /** 统计任务数量 **/
    public AppResponse getTaskNum(String token);

    /** 查询用户未完成的历史记录**/
    public AppResponse getUnTask(Integer page, Integer size,String token);

    /** 查询完成的历史记录 **/
    public AppResponse getFinishTask(Integer page, Integer size,String token);

    /**查询任务**/
    List<Map<String,Object>> queryTasks();
}
