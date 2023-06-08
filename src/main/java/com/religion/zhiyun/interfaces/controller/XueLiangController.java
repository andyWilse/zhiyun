package com.religion.zhiyun.interfaces.controller;

import com.religion.zhiyun.interfaces.service.XueLiangService;
import com.religion.zhiyun.utils.response.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/play")
public class XueLiangController {
    @Autowired
    private XueLiangService xueLiangService;

    @RequestMapping("/sync")
    public AppResponse playSync(@RequestParam("channelId") String channelId){
        return xueLiangService.getPlaySync(channelId);
    }
}
