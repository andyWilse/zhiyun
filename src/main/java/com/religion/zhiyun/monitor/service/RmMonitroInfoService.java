package com.religion.zhiyun.monitor.service;

import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.utils.RespPageBean;

import java.util.List;
import java.util.Map;

public interface RmMonitroInfoService {
    List<MonitroEntity> allMonitro();

    void addMonitro(MonitroEntity monitroEntity);

    void updateMonitro(MonitroEntity monitroEntity);

    void deleteMonitro(String monitroId);
    List<Map<String,Object>> getAllNum();
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

    /** 监控  **/
    RespPageBean getVenuesMonitor(String venuesName,String accessNumber);

    RespPageBean getMonitors(String venuesName,String accessNumber,String state);

}
