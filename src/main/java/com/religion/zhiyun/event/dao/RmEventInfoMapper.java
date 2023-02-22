package com.religion.zhiyun.event.dao;


import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.utils.response.AppResponse;
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
     * @param eventType
     * @return
     */
    List<Map<String,Object>> getByType(@Param("page") Integer page,
                                       @Param("size") Integer size,
                                       @Param("eventType") String eventType);

    List<Map<String,Object>> getAllNum();

    /* 分页查询
     * @param page
     * @param size
     * @param accessNumber
     * @return
     */
    List<VenuesEntity> getEventsByPage(@Param("page") Integer page, @Param("size") Integer size,
                                       @Param("accessNumber") String accessNumber);

    /**
     * 总条数
     * @return
     */
    Long getTotal();

    /**
     * 手机端（预警查询）
     * @return
     */
    List<Map<String,Object>> getEvents(String eventState,String eventType);


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

}
