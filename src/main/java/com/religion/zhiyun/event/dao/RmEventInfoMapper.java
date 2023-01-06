package com.religion.zhiyun.event.dao;


import com.religion.zhiyun.event.entity.EventEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    void deleteEvent(String eventId);

}
