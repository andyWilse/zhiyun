package com.religion.zhiyun.utils.enums;


import lombok.Getter;

@Getter
public enum ParamCode {
    //场所使用情况
    VENUES_STATUS_00("00", "未使用"),
    VENUES_STATUS_01("01", "使用中"),
    VENUES_STATUS_02("02", "已废弃"),

    //职员在职状态
    STAFF_STATUS_00("00", "入职中"),
    STAFF_STATUS_01("01", "在职"),
    STAFF_STATUS_02("02", "已离职"),

    //文件类型
    FILE_TYPE_01("01", "照片"),
    FILE_TYPE_02("02", "文件"),

    //事件类型
    EVENT_TYPE_01("01", "火灾预警"),
    EVENT_TYPE_02("02", "人脸识别"),
    EVENT_TYPE_03("03", "任务预警"),
    EVENT_TYPE_04("04", "人流聚集"),

    //事件状态
    EVENT_STATE_00("00", "待查实"),
    EVENT_STATE_01("01", "已完成"),
    EVENT_STATE_02("02", "一键上报"),
    EVENT_STATE_03("03", "拨打119"),
    EVENT_STATE_04("04", "误报解除"),

    //预警通知
    NOTIFIED_FLAG_00("00", "未通知"),
    NOTIFIED_FLAG_01("01", "已通知"),
    NOTIFIED_FLAG_02("02", "一键上报"),
    NOTIFIED_FLAG_03("03", "拨打119"),
    NOTIFIED_FLAG_04("04", "误报解除"),

    //摄像设备状态
    MONITOR_STATE_01("01", "在线"),
    MONITOR_STATE_02("02", "报修"),


    ;

    private String code;
    private String message;

    private ParamCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
