package com.religion.zhiyun.task.service;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.response.AppResponse;

import java.util.List;

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

}
