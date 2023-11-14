package com.religion.zhiyun.event.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiEntity {

    private String data;//数据

    private String alarmId;//告警事件ID
    private String applicationId;
    private String eventProcessor;
    private String areaCode;//事件所属区域编码。必输
    private String content;//事件详细内容。必输//没有事件级别，事件类型就是“人员聚集”、“发现重点人员”、“明火检测”
    private String deviceId;//通道编码
    private String deviceName;//通道名称
    private String eventCoordinate;//事件发生地经纬度，格式: 经度,纬度。必输
    private String eventPlaceName;//事件发生地名
    private String eventTime;//事件发生时间。必输
    private String executeType;//事件执行类型 1：流转 2：展示
    private String innerEventId;//第三方案件号。必输
    private String msgType;//消息类型。必输
    private String origin;// 消息来源部门名称例如：市应急局。必输
    private String receiveName;//接收方标识。必输

    private String streetCode;//街道编码
    private String scence;//消息场景编码。必输
    private String title;//消息标题。必输
    private String superviseStatus;//针对该事件处理状态 0：未处理 1：处理中 2：已完成

    private String eventUrl;// 事件跳转平台链接
    private String eventCreator;//事件消息上报人
    private String eventCreatorTel;//事件消息上报人联系方式
    private String eventDuration;//事件持续时间
    private String remindStatus;//事件是否超时: 0 未超时 1 已超时
    private String cdcProviderId;//消息提供方id

    //eventFile\":\"https://zjc-cw.sittone.com:51443/hook/v1/gb/algorithm/image?storageId=B5&
    // fileName=pkg30/rect/motorVehicleIllegalParkAreaDetection/20230607/33030400002000200826/33030400001310671922/23563759278_5618276.jpg\"
    private String eventFile;//单图片

}
