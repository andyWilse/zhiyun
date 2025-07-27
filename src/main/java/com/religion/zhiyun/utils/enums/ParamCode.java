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
    FILE_TYPE_03("03", "视频"),

    //事件文件类型
    //EVENT_FILE_00("00", "AI预警(视频)"),
    EVENT_FILE_01("01", "AI预警(图片)"),
    EVENT_FILE_02("02", "烟感"),

    //事件类型
    EVENT_LEVEL_01("01", "紧急"),
    EVENT_LEVEL_02("02", "普通"),
    //事件类型
    /*EVENT_TYPE_01("01", "火灾预警"),
    EVENT_TYPE_02("02", "人脸识别"),
    EVENT_TYPE_03("03", "任务预警"),
    EVENT_TYPE_04("04", "人流聚集"),*/
    EVENT_TYPE_01("01", "明火"),
    EVENT_TYPE_02("02", "超限"),
    EVENT_TYPE_03("03", "重点"),
    EVENT_TYPE_04("04", "聚集"),
    EVENT_TYPE_05("05", "设备报修"),
    EVENT_TYPE_06("06", "画面异常"),
    //事件状态
    EVENT_STATE_01("01", "已处理"),
    EVENT_STATE_02("02", "已上报"),
    EVENT_STATE_03("03", "已通知"),
    EVENT_STATE_04("04", "误报解除"),
    EVENT_STATE_05("05", "已推送民宗快响"),
    //预警通知
    NOTIFIED_FLAG_01("01", "已处理"),
    NOTIFIED_FLAG_02("02", "已上报"),
    NOTIFIED_FLAG_03("03", "已通知"),
    NOTIFIED_FLAG_04("04", "误报解除"),
    NOTIFIED_FLAG_05("05", "民宗快响推送"),
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
