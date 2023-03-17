package com.religion.zhiyun.event.service;

import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.OutInterfaceResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RmEventInfoService {

    OutInterfaceResponse addEvent(String eventEntity);

    List<EventEntity> allEvent();

    void updateEvent(EventEntity eventEntity);

    void deleteEvent(int eventId);

    EventEntity getByEventId(String eventId);

    AppResponse getByType(Integer page, Integer size, String eventType,String token);

    List<Map<String,Object>> getAllNum();

    /**
     * 分页查询
     * @param page
     * @param size
     * @param accessNumber
     * @return
     */
    RespPageBean getEventsByPage(Integer page, Integer size, String accessNumber,String token);

    /**
     * 手机端（预警查询）
     * @return
     */
    RespPageBean getUndoEvents(Integer page, Integer size,String token);

    /**
     * 手机端（预警查询详情）
     * @return
     */
    PageResponse getUndoEventDetail(String eventId);

    /**
     * 接收烟感器、燃气探测传输的数据
     * @param eventEntity
     */
    OutInterfaceResponse addEventByNB(String eventEntity);

    /**
     * 预警通知（浙里办）
     * @param page
     * @param size
     * @param eventState
     * @return
     */
    AppResponse getEventsByState(Integer page, Integer size, String eventState,String token);

    /**
     * 解除误报
     * @param eventId
     * @return
     */
    AppResponse dismissEvent(String eventId,String token);

    /**
     * 拨打119
     * @param eventId
     * @return
     */
    AppResponse callFire(String eventId,String token);

    /**
     * 一键上报（管理）
     * @param eventId
     * @return
     */
    AppResponse reportOne(String eventId,String token);

    /**
     * 一键上报（监管）
     * @param eventId
     * @return
     */
    AppResponse reportOneJg(String eventId,String token);

    /**
     * 月（10个月，传-9）
     * @param num
     * @return
     */
    AppResponse getEventsMonth(@Param("num") int num,String type,String token);

    /**
     * 日(10天，传-10)
     * @param num
     * @return
     */
    AppResponse getEventsDay(@Param("num") int num,String type,String token);

    /**
     * 周
     * @param num（10周，传-10）
     * @param dayNum（=7*(num+1)-1）
     * @return
     */
    AppResponse getEventsWeek(@Param("num") int num,@Param("dayNum") int dayNum,String type,String token);

    /**
     * 总计
     * @param dateType（周：week,月：month,日：day）
     * @return
     */
    AppResponse getEventsGather(int num,String dateType,String token);

}
