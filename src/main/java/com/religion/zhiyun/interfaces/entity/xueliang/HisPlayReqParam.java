package com.religion.zhiyun.interfaces.entity.xueliang;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HisPlayReqParam {

    //通道编码
    private String channelId;

    //设备编码
    private String deviceId;

    //开始时间
    private String startTime;

    //结束时间
    private String endTime;

    //接收 ip 地址
    private String ipAddr;

    //接收端口
    private int port;

    //下载速度
    private int speed;

    //流模式，1:udp0:tcp
    private int streamMode;


}
