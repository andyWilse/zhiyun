package com.religion.zhiyun.interfaces.entity.mas;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasErrEntity {
    private String message;
    private String status;
    private String rspcod;
    private Boolean success;
}
