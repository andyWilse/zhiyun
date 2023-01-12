package com.religion.zhiyun.sys.controller;


import com.religion.zhiyun.sys.entity.SysEntity;
import com.religion.zhiyun.sys.service.RmSysInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys")
@CrossOrigin
public class RmSysInfoController {
    @Autowired
    private RmSysInfoService rmSysInfoService;

    @RequestMapping("/allSys")
    public String allSys(){
        List<SysEntity> list = rmSysInfoService.allSys();
        return JsonUtils.objectTOJSONString(list);
    }

    @ResponseBody
    @PostMapping("/addSys")
    public String addSys(@RequestBody String sysJson){
        SysEntity sysEntity = JsonUtils.jsonTOBean(sysJson, SysEntity.class);
        rmSysInfoService.addSys(sysEntity);
        return "添加成功！";
    }

    @PostMapping("/updateSys")
    public void updateSys(@RequestBody String sysJson){
        SysEntity sysEntity = JsonUtils.jsonTOBean(sysJson, SysEntity.class);
        rmSysInfoService.updateSys(sysEntity);
    }

    @PostMapping("/deleteSys")
    public void deleteSys(){
        rmSysInfoService.deleteSys("sysId");
    }

}
