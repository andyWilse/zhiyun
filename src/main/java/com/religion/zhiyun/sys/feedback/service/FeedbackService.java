package com.religion.zhiyun.sys.feedback.service;

import com.religion.zhiyun.utils.response.AppResponse;

import java.util.Map;

public interface FeedbackService {
    /** 使用反馈 **/
    public AppResponse feedbackUse(Map<String, Object> map, String token);

    /** 坐标修改 **/
    public AppResponse axisUpdate(Map<String, Object> map, String token);
}
