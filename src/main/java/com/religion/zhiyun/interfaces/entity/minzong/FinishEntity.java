package com.religion.zhiyun.interfaces.entity.minzong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinishEntity {
    private String phone;//办结操作人手机号（需为浙政钉账号）,非必输
    private String fileUrl;//办结附件地址,非必输
    private String message;//办结文案,非必输
    private String finishTime;//办结时间,非必输
}
