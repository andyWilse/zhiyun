package com.religion.zhiyun.utils.sms.call.util;

import lombok.Setter;

@Setter
public class PlayContentInfo {

    /*非必输，通知语音的放音文件名。需要先通过放音文件管理页面上传放音文件并通过审核才能使用。
    当前系统只支持Wave格式的音频文件，文件如“notifyvoice.wav”。该参数和templateId是二选一的关系，即两个参数必须携带其中一个。*/
    private String notifyVoice;

    //非必输，语音通知模板ID，用于唯一标识语音通知模板。
    private String templateId;

    //非必输，语音通知模板的变量值列表，用于依次填充templateId参数指定的模板内容中的变量。该参数需填写为JSONArray格式。
    //private String templateParas;
    private String[] templateParas;
    //非必输，是否进行收号。若进行收号，请在语音通知内容播放完毕后的5秒之内进行按键操作，系统会将用户输入的号码通过语音通知呼叫状态通知API发送给SP
    private Integer collectInd;

    //非必输，当collectInd字段设置为非0时此参数有效。此字段用于设置是否在收号后重新播放notifyVoice、ttsContent或templateId指定的放音。
    private Boolean replayAfterCollection;

    //非必输，当replayAfterCollection字段设置为true时此参数有效。
    private String collectContentTriggerReplaying;
}
