package com.religion.zhiyun.interfaces.entity.mas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasResponse {
    //上行服务代码。
    private String addSerial;
    //上行手机号码，每批次携带一个号码。
    private String mobile;
    //上行短信发送时间，格式：yyyy-MM-dd HH:mm:ss。
    private String sendTime;
    //上行短信内容。
    private String smsContent;
}
