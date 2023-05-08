package com.religion.zhiyun.sys.feedback.controller;

import com.religion.zhiyun.sys.feedback.service.FeedbackService;
import com.religion.zhiyun.utils.response.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @RequestMapping("/my")
    public AppResponse feedbackUse(@RequestParam Map<String, Object> map, @RequestHeader("token")String token){
        return feedbackService.feedbackUse(map,token);
    }

    @RequestMapping("/axisUpdate")
    public AppResponse axisUpdate(@RequestParam Map<String, Object> map, @RequestHeader("token")String token){
        return feedbackService.axisUpdate(map,token);
    }
}
