package com.religion.zhiyun.record.controller;


import com.religion.zhiyun.record.entity.RecordEntity;
import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/record")
@CrossOrigin
public class OperateRecordController {
    @Autowired
    private OperateRecordService rmUserLogsInfoService;

    @GetMapping("/findByPage")
    public RespPageBean findByPage(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){

        String userName = (String)map.get("userName");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");

        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return rmUserLogsInfoService.findRecordByPage(page,size,userName,token);
    }


}
