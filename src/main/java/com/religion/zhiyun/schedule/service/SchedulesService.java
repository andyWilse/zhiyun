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
    /**
     * 系统运行计数
     */
    void metricRecord();

    /**
     * 预警未处理重复提醒
     */
    void repeatMessage();

    /**
     * 视频截图处理(历史视频)
     */
    void handleCutPicture();

    /**
     * 视频截图处理(实时)
     */
    void cutSyncPicture();

    /**
     * 公安短信获取：未成年（重点）预警
     */
    void getMasMessage();
}
