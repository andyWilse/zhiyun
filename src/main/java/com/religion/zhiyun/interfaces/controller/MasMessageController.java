package com.religion.zhiyun.interfaces.controller;


import com.religion.zhiyun.interfaces.service.AiEventService;
import com.religion.zhiyun.interfaces.service.MasMessageService;
import com.religion.zhiyun.login.http.inter.DecryptRequest;
import com.religion.zhiyun.login.http.inter.EncryptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@DecryptRequest(true)
@EncryptResponse(true)
@RestController
@Slf4j
@RequestMapping("/mas")
public class MasMessageController {
    @Autowired
    private MasMessageService masMessageService;
}
