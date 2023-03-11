package com.religion.zhiyun.monitor.dao;

import com.religion.zhiyun.monitor.entity.MoVenuesEntity;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
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

    /**
     * 监控数量统计
     * @param vo
     * @return
     */
    List<Map<String,Object>> getAllNum(@Param("vo")ParamsVo vo );

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

    /** 根据场所名字查看监控  **/
    List<Map<String,Object>> getVenuesMonitor(@Param("vo") ParamsVo vo);

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

    /**
     * 获取监控信息
     * @param accessNumber
     * @return
     */
    MonitroEntity getMonitorsList( @Param("accessNumber") String accessNumber);

}
