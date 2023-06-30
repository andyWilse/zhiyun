package com.religion.zhiyun.interfaces.entity.xueliang;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayEntity {

    //基本信息
    private String msg;// 提示信息
    private String code;//返回状态码（0：播放成功，301：当前通道繁忙，403：无权限访问，500：播放设备异常）
    private String data;//返回数据

    //data内容
    private String cahnnelId;//通道 id
    private String deviceID;//设备 id
    private String flv;//Flv 地址
    private String rtmp;//Rtmp 地址
    private String rtsp;//Rtsp 地址
    private String ssrc;//ssrc Ssrc 值
    private String ws_flv;//Ws_flv 地址
    private String wss_flv;//
    private String ip;//播放 ip 地址
    private String tracks;//音轨
    private String callId;//句柄 id
    private String hls;//hls 地址

    private String forwardPort;//hls 地址
    private String createTime;//
    private String mediaPort;//
    private String httpsFlv;//

}
