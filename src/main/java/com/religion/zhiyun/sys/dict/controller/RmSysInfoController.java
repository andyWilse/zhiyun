package com.religion.zhiyun.sys.dict.controller;


import com.religion.zhiyun.sys.dict.entity.SysEntity;
import com.religion.zhiyun.sys.dict.service.RmSysInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.PcResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dict")
public class RmSysInfoController {
    @Autowired
    private RmSysInfoService rmSysInfoService;

    @RequestMapping("/getSysDict")
    public RespPageBean getSysDict(@RequestParam("dictTypeCd") String dictTypeCd){
        return rmSysInfoService.getSysDict(dictTypeCd);
    }

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
