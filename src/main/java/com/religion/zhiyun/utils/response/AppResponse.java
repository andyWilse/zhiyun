package com.religion.zhiyun.utils.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppResponse {
    private long code;
    private String message;
    private Long total;
    private Object[] result;

    public AppResponse(){
        super();
    }
    public AppResponse(long code,String message){
        this.code=code;
        this.message=message;
    }
    public AppResponse(long code,String message,Object[]  result){
        this.code=code;
        this.message=message;
        this.result=result;
    }
    public AppResponse(long code,String message,Long total,Object[]  result){
        this.code=code;
        this.message=message;
        this.total=total;
        this.result=result;
    }

}
