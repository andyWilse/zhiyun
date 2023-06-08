package com.religion.zhiyun.interfaces.entity.minzong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitEntity {

    private String title;//事件标题,必输
    private String reporterPhone;//上报人手机号（需为浙政钉账号）,必输
    private String reporterName;//上报人姓名,必输

    private int eventType;//事件类型，详见数据字典,必输
    private int sceneId;//场景id，取自业务场景接口key,必输
    //1-村社（网格）级;2-乡（镇）级;3-县级;4-市级;5-省级;
    private int level;//事件等级，详见数据字典,必输;
    //1-浙政钉;2-浙里办;3-工作台;
    private String origion;//来源，详见数据字典,必输
    private String city;//地市,非必输
    private String citycode;//地市行政区划编码,非必输
    private String district;//区县,非必输
    private String adcode;//区县行政区划编码,非必输
    private String township;//乡镇街道,非必输
    private String towncode;//乡镇街道行政区划编码,非必输

    private Number longitude;//经度,必输
    private Number latitude;//纬度,必输

    private String locationAddress;//定位地址,非必输
    private String detailedAddress;//详细地址,非必输
    private String fileUrl;//文件地址，多个用逗号分隔,非必输
    private String occurTime;//事发时间,非必输
    private String contentDesc;//内容描述,非必输
    private String otherInfo;//其他信息,非必输
    private String name;//成员姓名,非必输
    private String phone;//成员手机号（需为浙政钉账号）,非必输
    private Object[] members;//相关处置成员,非必输

    private String flowType;


}
