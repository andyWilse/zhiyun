package com.religion.zhiyun.utils.response;

import com.religion.zhiyun.user.shiro.LoginInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.UsernamePasswordToken;

@Getter
@Setter
public class AppResponse {
    private long code;
    private String message;
    private Long total;
    private Object[] result;
    private String direct;
    private LoginInfo data;
    private UsernamePasswordToken token;


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

    public AppResponse(long code,String message,String direct){
        this.code=code;
        this.message=message;
        this.direct=direct;
    }
    public AppResponse(long code, String message, LoginInfo data, String direct, UsernamePasswordToken token){
        this.code=code;
        this.message=message;
        this.data=data;
        this.direct=direct;
        this.token=token;
    }
}