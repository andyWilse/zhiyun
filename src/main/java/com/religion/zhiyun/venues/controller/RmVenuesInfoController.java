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
@CrossOrigin
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



    //根据教派类别查询
    @RequestMapping("/querySectAll")
    public String querySectAll(String religiousSect) {
        List<VenuesEntity> list = rmVenuesInfoService.querySectAll(religiousSect);
        return JsonUtils.objectTOJSONString(list);
    }


    @RequestMapping("/getByVenuesFaculty")//有问题
    String getByVenuesFaculty(String venuesName,String responsiblePerson){
        List<VenuesEntity> list = rmVenuesInfoService.getByVenuesFaculty(venuesName, responsiblePerson);
        return JsonUtils.objectTOJSONString(list);
    }

    //根据id获取该教堂
    @RequestMapping("/getVenueByID")
    String getVenueByID(String venuesId){
        VenuesEntity venuesEntity = rmVenuesInfoService.getVenueByID(venuesId);
        return JsonUtils.objectTOJSONString(venuesEntity);
    }

    //教堂的数量展示
    @RequestMapping("/getAllNum")
    public String getAllNum(){
        List<Map<String, Object>> list = rmVenuesInfoService.getAllNum();
        return JsonUtils.objectTOJSONString(list);
    }

    //教堂的数量展示
    @RequestMapping("/getByResponsiblePerson")
    public String getByResponsiblePerson(String responsiblePerson){
        VenuesEntity byResponsiblePerson = rmVenuesInfoService.getByResponsiblePerson(responsiblePerson);
        return JsonUtils.objectTOJSONString(byResponsiblePerson);
    }

}
