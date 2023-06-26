package com.religion.zhiyun.monitor.dao;

import com.religion.zhiyun.monitor.entity.MonitorEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RmMonitroInfoMapper {
    List<MonitorEntity> allMonitro();

    void addMonitro(MonitorEntity monitroEntity);

    void updateMonitro(MonitorEntity monitroEntity);

    void deleteMonitro(String monitroId);

    /**
     * 监控数量统计
     * @param vo
     * @return
     */
    List<Map<String,Object>> getAllNum(@Param("vo")ParamsVo vo );

    List<MonitorEntity> getMonitorByState(String state);

    List<MonitorEntity> getMonitorByVenuesId(String state);

    /**
     * 分页查询
     * @param vo
     * @return
     */
    List<MonitorEntity> getMonitorByPage(@Param("vo") ParamsVo vo);

    /**
     * 总条数
     * @return
     */
    Long getTotal(@Param("vo") ParamsVo vo);

    //根据设备编号返回监控url
    String getMonitorURLByAccessNum(String accessNum);

    /** 根据场所名字查看监控  **/
    List<Map<String,Object>> getVenuesMonitor(@Param("vo") ParamsVo vo);
    List<Map<String,Object>> getMonitUrl(@Param("venuesName") String venuesName);
    /**
     * 总条数
     * @return
     */
    Long getVenuesMonitorTotal(@Param("vo") ParamsVo vo);

    /**
     * 获取监控信息
     * @param vo
     * @return
     */
    List<Map<String,Object>> getMonitors(@Param("vo") ParamsVo vo);

    /**
     * 获取监控信息
     * @param vo
     * @return
     */
    Long getMonitorsTotal(@Param("vo") ParamsVo vo);

    /**
     * 获取监控详情
     * @param accessNumber
     * @return
     */
    List<Map<String,Object>> getMoDetail(String accessNumber,String venuesId);
    List<Map<String,Object>> getMonitInfo(String accessNumber,String venuesId);

    /**
     * 获取监控信息
     * @param accessNumber
     * @return
     */
    List<MonitorEntity> getMonitorsList(@Param("accessNumber") String accessNumber);
    List<MonitorEntity> getMonitorInstList(@Param("procInstId") String procInstId);

}
