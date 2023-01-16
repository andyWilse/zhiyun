package com.religion.zhiyun.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RespPageBean {

    private Long total;
    private List<?> data;
    private Object[] datas;
    private Object[] dataone;

    private long code;
    private String result;
    private Object[] resultArr;

    public RespPageBean(){
        super();
    }

    public RespPageBean(long code){
        this.code=code;
    }
    public RespPageBean(long code,String result){
        this.code=code;
        this.result=result;
    }

    public RespPageBean(long code,Object[] resultArr){
        this.code=code;
        this.resultArr=resultArr;
    }

    public RespPageBean(long code,String result,Object[] resultArr){
        this.code=code;
        this.result=result;
        this.resultArr=resultArr;
    }

}
