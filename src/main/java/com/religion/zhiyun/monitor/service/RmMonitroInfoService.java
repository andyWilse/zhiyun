package com.religion.zhiyun.monitor.service;

import com.religion.zhiyun.monitor.entity.MonitorEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.List;
import java.util.Map;

public interface RmMonitroInfoService {
    List<MonitorEntity> allMonitro();

    void addMonitro(MonitorEntity monitroEntity);

    /**
     * 维修设备保存
     * @param repairJson
     */
    PageResponse addMonitorEvent(String repairJson);

    void deleteMonitro(String monitroId);

    /**
     * 监控数量统计（app监控）
     * @return
     */
    PageResponse getAllNum(String token);

    List<MonitorEntity> getMonitorByState(String state);
    List<MonitorEntity> getMonitorByVenuesId(String state);

    /**
     * 分页查询
     * @param map
     * @param token
     * @return
     */
    PageResponse getMonitorByPage(Map<String, Object> map, String token);

    String getMonitorURLByAccessNum(String accessNum);

    /** 场所（监控）  **/
    RespPageBean getVenuesMonitor(Integer page, Integer size,String venuesName,String token);

    PageResponse getMoDaPing(String venuesName);

    /**
     * 监控设备查询(app监控)
     * @param map
     * @param token
     * @return
     */
    RespPageBean getMonitors(Map<String, Object> map,String token);

    /**
     * 监控详情查询
     * @param search
     * @param type（01-地图 02-教职端监控）
     * @return
     */
    PageResponse getMoDetail(String search,String type);

    /**
     * 设备报修上报
     * @param map
     * @param token
     * @return
     */
    AppResponse monitRepairReport(Map<String, Object> map, String token);
    /**
     * 设备报修处理
     * @param map
     * @param token
     * @return
     */
    AppResponse monitRepairHandle(Map<String, Object> map, String token);



}
