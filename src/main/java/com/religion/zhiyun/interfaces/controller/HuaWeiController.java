package com.religion.zhiyun.interfaces.controller;

import com.religion.zhiyun.interfaces.service.HuaWeiService;
import com.religion.zhiyun.utils.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/huawei")
public class HuaWeiController {
    @Autowired
    private HuaWeiService huaWeiService;

    @RequestMapping("/callBack")
    public AppResponse CallBack(@RequestParam String re) {
        System.out.println(re);
        return new AppResponse(200,"ss");
    }

}
