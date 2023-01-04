package com.religion.zhiyun.venues.controller;


import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import com.religion.zhiyun.venues.services.RmVenuesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/venues")
public class RmVenuesInfoController {

    @Autowired
    private RmVenuesInfoService rmVenuesInfoService;

    @PostMapping("/insert")
    @ResponseBody
    public String insert(@RequestBody Map map) {
        System.out.println("");
        return "添加成功！";
    }

    @PostMapping("/add")
    @ResponseBody
    public String add(@RequestBody String venuesJson) {
        VenuesEntity venuesEntity = JsonUtils.jsonTOBean(venuesJson, VenuesEntity.class);
        rmVenuesInfoService.add(venuesEntity);
        return "添加成功！";
    }

    @PostMapping("/update")
    public void update(@RequestBody String venuesJson) {
        VenuesEntity venuesEntity=JsonUtils.jsonTOBean(venuesJson,VenuesEntity.class);
        rmVenuesInfoService.update(venuesEntity);
    }

    @PostMapping("/delete")
    public void delete() {
        rmVenuesInfoService.delete("venuesId");
    }

    @RequestMapping("/queryAll")
    public String getByResponsiblePerson() {
        List<VenuesEntity> list = rmVenuesInfoService.queryAll();
        return JsonUtils.objectTOJSONString(list);
    }

}
