package com.religion.zhiyun.login.entity;

import org.apache.shiro.subject.Subject;

public class Result {
    /**
     * 响应状态码
     */
    private int code;
    /**
     * 响应提示信息
     */
    private String message;
    /**
     * 响应结果对象
     */
    private LoginInfo loginInfo;

    /**
     * 响应提示信息
     */
    private Subject subject;
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Result(int code, String message, LoginInfo loginInfo) {
        this.code = code;
        this.message = message;
        this.loginInfo = loginInfo;
    }

    public Result(int code, String message, LoginInfo loginInfo, String name) {
        this.code = code;
        this.message = message;
        this.loginInfo = loginInfo;
        this.name=name;

    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}

