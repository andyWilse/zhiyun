package com.religion.zhiyun.record.service;

import com.religion.zhiyun.record.entity.RecordEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.List;
import java.util.Map;

public interface OperateRecordService {

    /**
     * 新增记录
     * @param logsEntity
     */
    void add(RecordEntity logsEntity);

    /**
     * 新增操作记录
     * @param map
     */
    AppResponse addRecord(Map<String, Object> map);

    /**
     * 分页查询（区级权限）
     * @return
     */
    RespPageBean findRecordByPage(Map<String, Object> map, String token);

    /**
     * 监控查看记录
     * @param map
     * @param token
     * @return
     */
    AppResponse addMonitRecord(Map<String, Object> map, String token);
}
