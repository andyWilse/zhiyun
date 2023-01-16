package com.religion.zhiyun.sys.file.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseVo<T> {

    private Integer status;

    private String msg;

    private T data;

    private ResponseVo(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ResponseVo(Integer status, T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> ResponseVo<T> success(T data){
        return new ResponseVo<T>(ResponseEnum.SUCCESS.getCode(), data);
    }

    public static <T> ResponseVo<T> error(ResponseEnum responseEnum){
        return new ResponseVo<T>(responseEnum.getCode() , responseEnum.getDesc());
    }
}
