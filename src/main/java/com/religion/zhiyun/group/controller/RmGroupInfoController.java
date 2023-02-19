package com.religion.zhiyun.group.controller;


import com.religion.zhiyun.group.service.RmGroupInfoService;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/group")
public class RmGroupInfoController {
    @Autowired
    private RmGroupInfoService rmGroupInfoService;

    @GetMapping("/allGroup")
    public RespPageBean getAllGroup(@RequestParam Map<String,Object> map){
        String groupName = (String)map.get("groupName");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return rmGroupInfoService.getPage(page,size,groupName);
    }
}
