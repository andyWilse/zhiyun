package com.religion.zhiyun.user.shiro;

/**
 * Created by WJ on 2019/3/26 0026
 */
public class ResultFactory {
    public static Result buildSuccessResult(LoginInfo data) {
        return buidResult(ResultCode.SUCCESS, "成功", data);
    }

    public static Result buildFailResult(String message) {
        return buidResult(ResultCode.FAIL, message, null);
    }

    public static Result buidResult(ResultCode resultCode, String message, LoginInfo data) {
        return buidResult(resultCode.code, message, data);
    }

    public static Result buidResult(int resultCode, String message, LoginInfo data) {
        return new Result(resultCode, message, data);
    }
}

