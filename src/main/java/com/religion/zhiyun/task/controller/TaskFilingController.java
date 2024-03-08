package com.religion.zhiyun.task.controller;

import com.religion.zhiyun.login.http.inter.DecryptRequest;
import com.religion.zhiyun.login.http.inter.EncryptResponse;
import com.religion.zhiyun.task.service.TaskFilingService;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 教职人员端的场景更新、活动备案，默认普通状态，只推给街镇干事审核
 */
@DecryptRequest(true)
@EncryptResponse(true)
@RestController
@RequestMapping("/filing")
public class TaskFilingController {

    @Autowired
    private TaskFilingService taskFilingService;

    @RequestMapping("/launch")
    @ResponseBody
    public AppResponse launch(@RequestBody String taskJson, @RequestHeader("token")String token){
        AppResponse launch =  taskFilingService.launch(taskJson,token);
        return launch;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public AppResponse handle(@RequestParam Map<String,Object> map,@RequestHeader("token")String token){
        AppResponse report = taskFilingService.handle(map,token);
        return report;
    }

    //获取历史备案
    @GetMapping("/fillHistory")
    public PageResponse getFillHistory(@RequestBody Map<String, Object> map, @RequestHeader("token")String token){
        String search = (String)map.get("search");
        Integer page = (Integer) map.get("page");
        Integer size = (Integer)map.get("size");
        //Integer page = Integer.valueOf(pages);
        //Integer size = Integer.valueOf(sizes);

        return taskFilingService.getFillHistory(page,size,search,token);
    }
    //获取历史备案详情
    @GetMapping("/fillHisDetail")
    public PageResponse getFillHistory(@RequestParam String filingId){
        return taskFilingService.getFillHisDetail(filingId);
    }


}
