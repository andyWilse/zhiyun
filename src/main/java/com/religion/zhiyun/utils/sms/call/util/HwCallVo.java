package com.religion.zhiyun.utils.sms.call.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HwCallVo {

    /**
     * 华为语音:固话号码，呼叫
     */
    @Getter
    private static String  displayNbr;
    @Value("${call.display.nbr}")
    public void setDisplayNbr(String displayNbr) {
        HwCallVo.displayNbr = displayNbr;
    }

    /**
     * 华为语音:app_Key
     */
    @Getter
    private static String  appKey;
    @Value("${call.app.key}")
    public void setAppKey(String appKey) {
        HwCallVo.appKey = appKey;
    }

    /**
     * 华为语音:app_Secret
     */
    @Getter
    private static String appSecret;
    @Value("${call.app.secret}")
    public void setAppSecret(String appSecret) {
        HwCallVo.appSecret = appSecret;
    }

    /**
     * 华为语音:呼叫地址
     */
    @Getter
    private static String  voiceUrl;
    @Value("${call.voice.url}")
    public  void setVoiceUrl(String voiceUrl) {
        HwCallVo.voiceUrl = voiceUrl;
    }





}

