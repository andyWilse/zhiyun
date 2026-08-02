package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum TaskActEnums {
    AI_WARN_NODE_01("10030001", "预警发起","aiStart"),
    AI_WARN_NODE_02("10030002", "人工审核","review"),
    AI_WARN_NODE_03("10030003", "基层处理","manage"),
    AI_WARN_NODE_04("10030004", "终审评价","final"),
    AI_WARN_NODE_05("10030005", "预警结束","aiEnd"),
    AI_WARN_NODE_06("10030006", "退回","back"),
    AI_WARN_NODE_07("10030007", "误报解除","out"),

    AI_NODE_STATE_00("00", "未处理",""),
    AI_NODE_STATE_01("01", "已处理",""),

    ASS_STATE_00("00", "删除",""),
    ASS_STATE_01("01", "新增",""),
    ASS_STATE_02("02", "修改",""),

    //AI事件状态
    AI_WARN_STATE_00("00", "已接收",""),
    AI_WARN_STATE_01("01", "已复核",""),
    AI_WARN_STATE_02("02", "已处置",""),
    AI_WARN_STATE_03("03", "已评价结束",""),
    AI_WARN_STATE_04("04", "已退回",""),
    AI_WARN_STATE_05("05", "误报解除",""),




    ;

    private String code;
    private String chinaNm;
    private String englishNm;

    TaskActEnums(String code, String chinaNm, String englishNm) {
        this.code = code;
        this.chinaNm = chinaNm;
        this.englishNm = englishNm;
    }
}
