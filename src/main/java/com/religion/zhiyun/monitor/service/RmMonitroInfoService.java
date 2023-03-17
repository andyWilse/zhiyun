package com.religion.zhiyun.monitor.service;

import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.List;
import java.util.Map;

public interface RmMonitroInfoService {
    List<MonitroEntity> allMonitro();

    void addMonitro(MonitroEntity monitroEntity);

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

    List<MonitroEntity> getMonitorByState(String state);
    List<MonitroEntity> getMonitorByVenuesId(String state);

    /**
     * 分页查询
     * @param page
     * @param size
     * @param accessNumber
     * @return
     */
    RespPageBean getMonitrosByPage(Integer page, Integer size, String accessNumber);

    String getMonitorURLByAccessNum(String accessNum);

    /** 场所（监控）  **/
    RespPageBean getVenuesMonitor(Integer page, Integer size,String venuesName,String token);

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


}
