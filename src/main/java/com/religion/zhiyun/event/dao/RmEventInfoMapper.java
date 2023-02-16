package com.religion.zhiyun.event.dao;


import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RmEventInfoMapper {
    /**
     * 新增
     * @param eventEntity
     */
    void addEvent(EventEntity eventEntity);

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
    List<Map<String,Object>> getByType(@Param("eventType") String eventType);

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
     * 接收烟感器传输的数据
     * @param eventEntity
     */
    void addEventByNB(EventEntity eventEntity);

}
