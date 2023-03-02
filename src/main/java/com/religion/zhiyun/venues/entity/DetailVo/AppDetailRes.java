package com.religion.zhiyun.venues.entity.DetailVo;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class AppDetailRes {
    private long code;
    private String message;
    private Map<String, Object> result;

    public AppDetailRes(long code,String message,Map<String, Object> result){
        this.code=code;
        this.message=message;
        this.result=result;
    }
}
