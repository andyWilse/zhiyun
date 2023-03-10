package com.religion.zhiyun.event.dao;


import com.religion.zhiyun.event.entity.EventEntity;
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
    List<EventEntity> allEventByState();

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

    List<Map<String,Object>> getAllNum();

    /* 分页查询
     * @param vo
     * @return
     */
    List<EventEntity> getEventsByPage(@Param("vo") ParamsVo vo);

    /**
     * 总条数
     * @return
     */
    Long getTotal(@Param("vo") ParamsVo vo);

    /**
     * 手机端（预警查询）
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
    void addEventByNB(EventEntity eventEntity);

    /**
     * 预警通知（浙里办）
     * @param page
     * @param size
     * @param eventState
     * @return
     */
    List<Map<String, Object>> getEventsByState(Integer page, Integer size,
                                               @Param("eventStates") String[] eventState,
                                               String login);

    /**
     * 总条数（浙里办）
     * @return
     */
    Long getTotalByState(@Param("eventStates") String[] eventState,String login);


    /**
     *更新事件表状态
     * @param eventId
     * @return
     */
    void updateEventState(String eventId, Date ymdHms,String eventState);

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
     * @param num
     * @return
     */
    List<Map<String,Object>> getEventsMonth(@Param("num") int num,@Param("eventType") String eventType);

    /**
     * 日(10天，传-10)
     * @param num
     * @return
     */
    List<Map<String,Object>> getEventsDay(@Param("num") int num,@Param("eventType") String eventType);

    /**
     * 周
     * @param num（10周，传-10）
     * @param dayNum（=7*(num+1)-1）
     * @return
     */
    List<Map<String,Object>> getEventsWeek(@Param("num") int num,@Param("dayNum") int dayNum,@Param("eventType") String eventType);


    /**
     * 月（10个月，传-9）
     * @param num
     * @return
     */
    List<Map<String,Object>> getEventsMonthGather(@Param("num") int num);

    /**
     * 日(10天，传-10)
     * @param num
     * @return
     */
    List<Map<String,Object>> getEventsDayGather(@Param("num") int num);

    /**
     * 周
     * @param num（10周，传-10）
     * @param （=7*(num+1)-1）
     * @return
     */
    List<Map<String,Object>> getEventsWeekGather(@Param("num") int num);


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

}
