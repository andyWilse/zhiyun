package com.religion.zhiyun.initdata;

import com.religion.zhiyun.utils.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/init")
public class InitController {

    @Autowired
    private InitService InitService;

    @RequestMapping("/uv")
    public AppResponse userVenues(){
        return InitService.userVenues();
    }


}


