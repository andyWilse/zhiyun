package com.religion.zhiyun.interfaces.service;

import com.religion.zhiyun.utils.response.AppResponse;

import javax.servlet.http.HttpServletRequest;

public interface HuaWeiService {

    AppResponse handleCallStatus(HttpServletRequest request);

    AppResponse handleFee(String requestBody);
}
