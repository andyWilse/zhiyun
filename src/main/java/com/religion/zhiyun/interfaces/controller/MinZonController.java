package com.religion.zhiyun.interfaces.controller;

import com.religion.zhiyun.interfaces.service.MinZonService;
import com.religion.zhiyun.utils.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/response")
public class MinZonController {
    @Autowired
    private MinZonService minZonService;

    @RequestMapping("/submit")
    public AppResponse submitEvent(@RequestParam("procInstId") String procInstId,@RequestHeader("token")String token){
        return minZonService.submitEvent(procInstId,token);
    }
    @RequestMapping("/finish")
    public AppResponse finishEvent(@RequestParam("procInstId") String procInstId,@RequestHeader("token")String token){
        return minZonService.finishEvent(procInstId,token);
    }

    @RequestMapping("/sum")
    public AppResponse mzSubmitSum(){
        return minZonService.mzSubmitSum();
    }


}
