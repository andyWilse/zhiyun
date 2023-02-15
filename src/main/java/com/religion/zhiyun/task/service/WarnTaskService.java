package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.RespPageBean;

import java.util.List;
import java.util.Map;

public interface WarnTaskService {
    /** 流程部署。部署一次就可以了。**/
    public Object deployment();

    /** 提交申请(发起) **/
    public Object launch(TaskEntity taskEntity);

    /** 提交申请(上报) **/
    public Object report(String procInstId);

    /** 流程结束 **/
    public Object handle(String procInstId,String handleResults);

    /** 获取未完成任务 **/
    public RespPageBean getTasking(Integer page, Integer size,String taskName, String taskContent);

    /** 统计任务数量 **/
    public String getTaskNum();

    /** 查询用户未完成的历史记录**/
    public Object getUnTask();

    /** 查询完成的历史记录 **/
    public Object getFinishTask();

    /**查询任务**/
    List<Map<String,Object>> queryTasks();
}
