package com.religion.zhiyun.interfaces.entity.huawei;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeInfo {

    private Integer direction;//必输,通话的呼叫方向，以语音通话平台为基准。● 0：外呼● 1：回呼

    private String spId;//必输,客户的云服务账号。

    private String appKey;//必输,应用的app_key。

    private String icid;//呼叫记录的唯一标识。

    private String bindNum;//必输,发起此次呼叫的固话号码。号码仅支持全局号码格式（包含国家码），比如+8675528****02。

    private String sessionId;//必输,通话链路的唯一标识。

    private String callerNum;//必输,主叫号码

    private String calleeNum;//必输,被叫号码

    private String callEndTime;//非必输，呼叫结束时间。该参数为UTC时间（+8小时为北京时间），时间格式为“yyyy-MM-ddHH:mm:ss”。

    private Integer fwdUnaswRsn;//非必输，转接呼叫操作失败的Q850原因值。

    private String failTime;//非必输，呼入、呼出的失败时间。该参数为UTC时间（+8小时为北京时间），时间格式为“yyyy-MM-ddHH:mm:ss”。

    private Integer ulFailReason;//非必输，通话失败的拆线点。

    private Integer sipStatusCode;//非必输，呼入、呼出的失败SIP状态码。

    private String callOutStartTime;//非必输，Initcall的呼出开始时间

    private String callOutAlertingTime;//非必输，Initcall的呼出振铃时间

    private String callOutAnswerTime;//非必输，Initcall的呼出应答时间

    private Integer callOutUnaswRsn;//非必输， Initcall的呼出失败的Q850原因值。

    private String dynIVRStartTime;//非必输，自定义动态IVR开始时间。

    private String dynIVRPath;//非必输，自定义动态IVR按键路径。用户每次的按键之间使用“-”链接，形式如下：0-2-3．

    private Integer recordFlag;//非必输，该字段用于录音标识，参数值范围如：● 0：表示未录音● 1：表示有录音

    private Integer ttsPlayTimes;//非必输，应用TTS功能时，使用TTS的总次数。

    private Integer ttsTransDuration;//非必输，应用TTS功能时，TTS Server进行TTS转换的总时长。单位为秒。

    private String serviceType;//非必输，携带呼叫的业务类型信息，取值范围：001：语音播放

    private String hostName;//非必输，该参数用于标识话单生成的服务器设备对应的主机名。

    private String userData;//非必输，用户附属信息，此参数的值与“语音通知API”中的"userData"参数值一致。

    private String callDetail;
    private String eventType;
    private String refEventId;

}
