package com.religion.zhiyun.utils.sms.sm.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendParam {

    /**
     * 腾讯短信发送：地址
     */
    @Getter
    private static String  smsUrl;
    @Value("${sms.send.url}")
    public void setSmsUrl(String smsUrl) {
        SendParam.smsUrl = smsUrl;
    }

    /**
     * 腾讯短信发送：用户名
     */
    @Getter
    private static String  userName;
    @Value("${sms.send.user.name}")
    public void setUserName(String userName) {
        SendParam.userName = userName;
    }

    /**
     * 腾讯短信发送：密码
     */
    @Getter
    private static String  password;
    @Value("${sms.send.user.password}")
    public void setPassword(String password) {
        SendParam.password = password;
    }

    /**
     * 腾讯短信发送：批量短信发送
     */
    @Getter
    private static String  massSend;
    @Value("${sms.send.mass}")
    public void setMassSend(String massSend) {
        SendParam.massSend = massSend;
    }

    /**
     * 腾讯短信发送：单条短信发送
     */
    @Getter
    private static String  oneSend;
    @Value("${sms.send.one}")
    public void setOneSend(String oneSend) {
        SendParam.oneSend = oneSend;
    }

}
