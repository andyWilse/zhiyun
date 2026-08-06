package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;

import java.util.List;
import java.util.Map;

public interface TaskAiWarnService {
    /** 流程发起 **/
    public AppResponse launch(TaskEntity taskEntity, List<String> userList, String loginNm);

    /** 人工审核：审核通过短信通知基层管理人员 **/
    public AppResponse review(String review,String procInstId,String token);

    /** 反馈处置 **/
    public AppResponse handle(String procInstId, String handleResults, String feedBack, String picture,String token);

    /** 评价 **/
    public AppResponse evaluate(String procInstId, String evaluation,String token);

    /** 退回 **/
    public AppResponse backup(String procInstId, String evaluation,String token);

    /** 误报解除 **/
    public AppResponse dismissAI(String procInstId,String token);

    /** 获取流程处理节点 **/
    public AppResponse getAiTaskAct(String procInstId);

    /** 删除流程处理节点 **/
    public AppResponse deleteTaskAct(int actId,String token);

    /** 获取流程处理节点 **/
    public PageResponse getTaskActDetail(int actId);

    /** 流程修改保存 **/
    public AppResponse saveTaskAct(Map<String, Object> map,String token);

    /** 删除预警任务接收人 **/
    public AppResponse deleteTaskAss(int actId,String token);

    /** 流程新增接收人 **/
    public AppResponse saveTaskAss(Map<String, Object> map,String token);

}
