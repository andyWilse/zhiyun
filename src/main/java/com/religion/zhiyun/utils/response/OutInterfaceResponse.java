package com.religion.zhiyun.utils.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutInterfaceResponse {
    private long code;
    private String message;
    private String direct;


    public OutInterfaceResponse(){
        super();
    }

    public OutInterfaceResponse(long code,String message){
        this.code=code;
        this.message=message;
    }

    public OutInterfaceResponse(long code,String message,String direct) {
        this.code = code;
        this.message = message;
        this.direct = direct;
    }
}
