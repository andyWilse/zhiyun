package com.religion.zhiyun.record.service;

import com.religion.zhiyun.record.entity.RecordEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.List;
import java.util.Map;

public interface OperateRecordService {

    void add(RecordEntity logsEntity);

    /**
     * 分页查询
     * @param page
     * @param size
     * @param userName
     * @return
     */
    RespPageBean findRecordByPage(Integer page, Integer size, String userName, String token);

    /**
     * 监控查看记录
     * @param map
     * @param token
     * @return
     */
    AppResponse addMonitRecord(Map<String, Object> map, String token);
}
