package com.religion.zhiyun.event.dao;

import com.religion.zhiyun.event.entity.NotifiedEntity;
import com.religion.zhiyun.interfaces.entity.huawei.FeeInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;


@Mapper
@Repository
public interface EventNotifiedMapper {

    /**
     * 新增
     * @param notifiedEntity
     */
    void addNotified(NotifiedEntity notifiedEntity);

    /**
     * 更新通知
     * @param eventId
     * @return
     */
    void updateNotifiedFlag(String eventId, String ymdHms,String notifiedFlag);
    void updateNotifiedUser(String eventId, String ymdHms,String notifiedUser);

    /**
     * 获取电话通知人
     */
    Map<String,String> getNotified();

    /**
     *新增语音通话信息
     * @param feeInfo
     */
    void addCall(FeeInfo feeInfo);

    /**
     * 更新语音通话信息
     * @param feeInfo
     */
    int updateCall(FeeInfo feeInfo);

}
