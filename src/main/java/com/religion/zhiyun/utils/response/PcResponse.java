package com.religion.zhiyun.utils.response;

public class PcResponse {
    private long code;
    private String message;
    private Object[] data;

    public PcResponse(long code,String message){
        this.code=code;
        this.message=message;
    }

    public PcResponse(long code,String message,Object[] data){
        this.code=code;
        this.message=message;
        this.data=data;

    }

}
