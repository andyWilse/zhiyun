package com.religion.zhiyun.monitor.dao;

import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RmMonitroInfoMapper {
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
    List<MonitroEntity> getMonitrosByPage(@Param("page") Integer page, @Param("size") Integer size,
                                         @Param("accessNumber") String accessNumber);

    /**
     * 总条数
     * @return
     */
    Long getTotal();

    //根据设备编号返回监控url
    String getMonitorURLByAccessNum(String accessNum);

    /** 监控  **/
    List<Map<String,Object>> getVenuesMonitor(String venuesName,String accessNumber);

    List<Map<String,Object>> getMonitors(String venuesName, String accessNumber, String state);


}
