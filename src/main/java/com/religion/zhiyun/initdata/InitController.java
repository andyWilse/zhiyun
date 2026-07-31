package com.religion.zhiyun.initdata;

import com.religion.zhiyun.login.http.inter.DecryptRequest;
import com.religion.zhiyun.login.http.inter.EncryptResponse;
import com.religion.zhiyun.utils.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@DecryptRequest(true)
@EncryptResponse(true)
@RestController
@Slf4j
@RequestMapping("/init")
public class InitController {

    @Autowired
    private InitService InitService;
    @EncryptResponse(false)
    @RequestMapping("/uv")
    public AppResponse userVenues(){
        return InitService.userVenues();
    }


}


