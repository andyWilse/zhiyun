package com.religion.zhiyun.login.token;

import com.religion.zhiyun.login.entity.LoginResp;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//@ControllerAdvice可以实现全局异常处理，可以简单理解为增强了的controller
@ControllerAdvice
public class MyExceptionHandler {

    //捕获AuthorizationException的异常
    @ExceptionHandler(value = AuthorizationException.class)
    @ResponseBody
    public LoginResp handleException(AuthorizationException e) {
        LoginResp msg= LoginResp.denyAccess("权限不足！！！！！");
        return msg;
    }
}
