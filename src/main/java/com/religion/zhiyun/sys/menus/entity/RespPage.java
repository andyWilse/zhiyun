package com.religion.zhiyun.sys.menus.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespPage {
    private long code;
    private String message;
    private MenuList data;

    public RespPage(long code,String message,MenuList data){
        this.code=code;
        this.message=message;
        this.data=data;
    }
}
