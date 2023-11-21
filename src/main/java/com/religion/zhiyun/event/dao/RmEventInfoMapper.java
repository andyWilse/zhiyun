package com.religion.zhiyun.event.dao;


import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.entity.EventReportMenEntity;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RmEventInfoMapper {
    /**
     * 新增
     * @param eventEntity
     */
    int addEvent(EventEntity eventEntity);

    //查询
    List<EventEntity> allEvent();

    /**
     * 修改
     * @param eventEntity
     */
    void updateEvent(EventEntity eventEntity);

    /**
     * 删除
     * @param eventId
     */
    void deleteEvent(int eventId);

    /**
     * 根据未完成的状态展示
     * @return
     */
    List<EventEntity> getEventByVo(@Param("vo") ParamsVo vo);

    /**
     * 根据事件id查询事件、场所、监控设备信息
     * @param eventId
     * @return
     */
    EventEntity getByEventId(String eventId);

    /**
     * 根据类型查看
     * @param vo
     * @return
     */
    List<Map<String,Object>> getByType(@Param("vo") ParamsVo vo);
    List<Map<String,Object>> getByTypeEvent(@Param("vo") ParamsVo vo);
    Long getByTypeEventTotal(@Param("vo") ParamsVo vo);

    /**
     * 事件数量统计
     * @return
     */
    List<Map<String,Object>> getAllNum(@Param("vo") ParamsVo vo);

    /* 分页查询
     * @param vo
     * @return
     */
    List<Map<String,Object>> getEventsByPage(@Param("vo") ParamsVo vo);

    /**
     * 总条数
     * @return
     */
    Long getTotal(@Param("vo") ParamsVo vo);

    /**
     * 手机端（预警查询）
     * 已通知，待核查
     * @return
     */
    List<Map<String,Object>> getUndoEvents(@Param("vo") ParamsVo vo);


    /**
     * 总条数,手机端（预警查询）
     * @return
     */
    Long getUndoEventsTotal(@Param("vo") ParamsVo vo);

    /**
     * 接收烟感器、燃气探测传输的数据
     * @param eventEntity
     */
    Long addEventByNB(EventEntity eventEntity);

    /**
     * 预警通知（浙里办）
     * @param vo
     * @return
     */
    List<Map<String, Object>> getEventsByState(@Param("vo") ParamsVo vo);

    /**
     * 总条数（浙里办）
     * @return
     */
    Long getTotalByState(@Param("vo") ParamsVo vo);


    /**
     *更新事件表状态
     * @param event
     * @return
     */
    void updateEventState(EventEntity event);

    /**
     * 获取事件
     * @param eventId
     * @return
     */
    EventEntity getEventById(@Param("eventId") String eventId);

    /**
     * 月
     * @param month
     * @return
     */
    List<Map<String,Object>> getEventsByMonth(@Param("month") String month);

    /**
     * 日
     * @param day
     * @return
     */
    List<Map<String,Object>> getEventsByDay(@Param("day") String day);

    /**
     * 月（10个月，传-9）
     * @param vo
     * @return
     */
    List<Map<String,Object>> getEventsMonth(@Param("vo") ParamsVo vo);

    /**
     * 日(10天，传-10)
     * @param vo num
     * @return
     */
    List<Map<String,Object>> getEventsDay(@Param("vo") ParamsVo vo);

    /**
     * 周
     * @param vo num（10周，传-10）
     * @param vo dayNum（=7*(num+1)-1）
     * @return
     */
    List<Map<String,Object>> getEventsWeek(@Param("vo") ParamsVo vo);


    /**
     * 月（10个月，传-9）
     * @param vo
     * @return
     */
    List<Map<String,Object>> getEventsMonthGather(@Param("vo") ParamsVo vo);

    /**
     * 日(10天，传-10)
     * @param vo
     * @return
     */
    List<Map<String,Object>> getEventsDayGather(@Param("vo") ParamsVo vo);

    /**
     * 周
     * @param vo（10周，传-10）
     * @param （=7*(num+1)-1）
     * @return
     */
    List<Map<String,Object>> getEventsWeekGather(@Param("vo") ParamsVo vo);


    /**
     * 修改
     * @param eventEntity
     */
    EventEntity queryEvent(EventEntity eventEntity);

    List<SysUserEntity> getMobile(@Param("venuesId")String venuesId);

    /**
     * 根据事件id查询事件、场所、监控设备信息
     * @param eventId
     * @return
     */
    List<Map<String,Object>> getEventDetail(@Param("eventId") String eventId);

    /**
     * 查询十分钟没人处理
     * String quarter,String month,String week ,String second
     * @return
     */
    List<EventEntity> findFillEvent(String day,String hour,String minute);

    /**
     * 保存一键上报 上报人信息
     * @param ev
     */
    void addEventReportMen(EventReportMenEntity ev);
    long queryEventReportMen(@Param("login") String login,@Param("procInstId") String procInstId);

    /**
     * 预警详情展示上报人
     */
    List<Map<String,Object>> getEventReportMen(@Param("eventId") String eventId);

    /**
     * 获取预警当前状态
     * @param eventId
     * @return
     */
    String getEventState(@Param("eventId") String eventId);

    /**
     * 预警数据查询（大屏）
     * @param vo
     * @return
     */
    List<Map<String,Object>> getEventDp(@Param("vo") ParamsVo vo);
    Long getEventDpTotal(@Param("vo") ParamsVo vo);

    /**
     * 预警汇总（大屏）
     * @return
     */
    List<Map<String,Object>> getAiSummary();
    List<Map<String,Object>> getAiTownSummary();

    /**
     * 推送民宗快响：数据统计
     * @param eventState
     * @return
     */
    Long getMzSubmitSum(@Param("eventState") String eventState);
}
