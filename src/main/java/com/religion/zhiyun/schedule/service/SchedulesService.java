package com.religion.zhiyun.schedule.service;

public interface SchedulesService {
    /**
     * 十分钟没人解除预警且无人上报，则由发起人自动上报
     */
    void UrgentAutoReport();

    /**
     * 十天后预警无人处理，则短信通知
     */
    void CommonAutoFill();
}
