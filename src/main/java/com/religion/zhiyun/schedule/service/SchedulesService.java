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

    /**
     * 同一条预警事件，会推送给多人，所有人都没有接听的话，每隔5分钟拨打一次，只要其中有一人接听成功了，或者在手机端处置完成了，就都停止拨
     */
    void warnCallReport();
}
