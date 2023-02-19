package com.religion.zhiyun.utils.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutInterfaceResponse {
    private long code;
    private String message;

    public OutInterfaceResponse(){
        super();
    }

    public OutInterfaceResponse(long code,String message){
        this.code=code;
        this.message=message;
    }
}
