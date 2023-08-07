package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.Map;

public interface TaskService {
    /** 流程部署。部署一次就可以了。**/
    public AppResponse deployment(String taskKey);

    /*** 获取流程部署信息  **/
    RespPageBean getProcdef(Integer page,Integer size,String taskName);

    /*** 获取维修设备任务 **/
    PageResponse getRepairTask(Map<String, Object> map,String token);

    /*** 获取维修设备详情  **/
    PageResponse getMonitorTask(String taskId);

    /**
     * 折线图总计
     * @param dateType（周：week,月：month,日：day）
     * @return
     */
    AppResponse getTaskZxt(int num,String dateType,String token);
    /**
     * 折线图总计
     * @param
     * @return
     */
    AppResponse getTaskGather(int num,String token);

    /** 获取未完成任务 **/
    public PageResponse getTasking(Map<String, Object> map,String token);

    /*** 获取流程流转意见 **/
    PageResponse getTaskCommon(String procInstId,String token);

    /*** APP我的任务 **/
    public PageResponse getMyTask(Map<String, Object> map,String token);
    /*** APP我的任务(首页，非Ai) **/
    public PageResponse getFirstTask(Map<String, Object> map,String token);

    /*** PC任务 **/
    public PageResponse getPcTask(Map<String, Object> map,String token);

    /*** APP我的任务详情 **/
    public PageResponse getTaskDetail(String procInstId,String token);
}
