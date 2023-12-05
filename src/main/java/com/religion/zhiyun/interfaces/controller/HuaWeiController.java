package com.religion.zhiyun.interfaces.controller;

import com.religion.zhiyun.interfaces.entity.huawei.RepeatedlyReadRequestWrapper;
import com.religion.zhiyun.interfaces.service.HuaWeiService;
import com.religion.zhiyun.utils.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/huawei")
public class HuaWeiController {
    @Autowired
    private HuaWeiService huaWeiService;

    @RequestMapping("/callStatus")
    public AppResponse callStatus(HttpServletRequest request){
        return huaWeiService.handleCallStatus(request);
    }

    @RequestMapping("/callFee")
    public AppResponse callFee(HttpServletRequest request){
        AppResponse appResponse =null;
        try {
            RepeatedlyReadRequestWrapper fee=new RepeatedlyReadRequestWrapper(request);
            String requestBody = fee.getBody();
            log.info("CallFeeBody:"+requestBody);
            appResponse = huaWeiService.handleFee(requestBody);
        } catch (IOException e) {
            e.printStackTrace();
            return new AppResponse(500,"语音通知话单通知接收失败！");
        }
        return appResponse;
    }

}
