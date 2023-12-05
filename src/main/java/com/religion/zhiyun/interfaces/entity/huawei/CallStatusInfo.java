package com.religion.zhiyun.interfaces.entity.huawei;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallStatusInfo {

    private String timestamp;

    private String userData;

    private String sessionId;

    private String caller;

    private String called;

    private Integer stateCode;

    private String stateDesc;

}
