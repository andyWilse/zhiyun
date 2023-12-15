package com.religion.zhiyun.interfaces.controller;

import com.religion.zhiyun.interfaces.service.AiEventService;
import com.religion.zhiyun.utils.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/ai")
public class AiEventController {

    @Autowired
    private AiEventService aiEventService;

    @RequestMapping("/getImage")
    public AppResponse getImage(@RequestParam("fileId") String fileId, @RequestHeader("token")String token){
        return aiEventService.getAiFile(fileId);
    }

    @RequestMapping("/downImage")
    public AppResponse downImage(@RequestParam("filePath") String filePath){
        return aiEventService.downImage(filePath,"");
    }

}


