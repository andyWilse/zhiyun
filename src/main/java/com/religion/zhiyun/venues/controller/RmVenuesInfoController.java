package com.religion.zhiyun.venues.controller;


import com.religion.zhiyun.login.api.CommonResult;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.DetailVo.AppDetailRes;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import com.religion.zhiyun.venues.services.RmVenuesInfoService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
//@RequiresPermissions ("venues:all")
//@CrossOrigin
//@RequiresPermissions(value={"venues:get"},logical = Logical.OR)
@RestController
@RequestMapping("/venues")
public class RmVenuesInfoController {

    @Autowired
    private RmVenuesInfoService rmVenuesInfoService;

    @PostMapping("/add")
    @ResponseBody
    public RespPageBean add(@RequestBody VenuesEntity venuesEntity,@RequestHeader("token")String token) {
        return rmVenuesInfoService.add(venuesEntity,token);
    }

    @PostMapping("/updateVenues")
    @ResponseBody
    public CommonResult updateVenues(@RequestBody VenuesEntity venuesEntity) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        venuesEntity.setLastModifyTime(timestamp);
        venuesEntity.setLastModifier("last");
        rmVenuesInfoService.update(venuesEntity);
        return CommonResult.success("修改成功！");
    }

    @PostMapping("/update")
    public void update(@RequestBody String venuesJson) {
        VenuesEntity venuesEntity=JsonUtils.jsonTOBean(venuesJson,VenuesEntity.class);
        rmVenuesInfoService.update(venuesEntity);
    }

    @PostMapping("/delete/{venuesId}")
    public void delete(@PathVariable int venuesId) {
        rmVenuesInfoService.delete(venuesId);
    }

    @RequestMapping("/querySelect")
    public RespPageBean querySelect(@RequestParam String search,@RequestParam String town) {
        return rmVenuesInfoService.querySelect(search,town);
    }

    //app下拉使用
    @RequestMapping("/getVenues")
    public AppResponse getVenuesList(@RequestParam String search,@RequestHeader("token")String token) {
        return rmVenuesInfoService.queryVenues(search);
    }

    //根据教派类别查询
    @RequestMapping("/querySectAll")
    public String querySectAll(String religiousSect) {
        List<VenuesEntity> list = rmVenuesInfoService.querySectAll(religiousSect);
        return JsonUtils.objectTOJSONString(list);
    }

    //根据id获取该教堂
    @RequestMapping("/getVenueByID")
    String getVenueByID(String venuesId){
        VenuesEntity venuesEntity = rmVenuesInfoService.getVenueByID(venuesId);
        return JsonUtils.objectTOJSONString(venuesEntity);
    }

    //统计场所数量（app首页）
    @RequestMapping("/getAllNum")
    public PageResponse getAllNum(@RequestHeader("token")String token){
        return rmVenuesInfoService.getAllNum(token);
    }

    //教堂的数量展示
    @RequestMapping("/getByResponsiblePerson")
    public String getByResponsiblePerson(String responsiblePerson){
        VenuesEntity byResponsiblePerson = rmVenuesInfoService.getByResponsiblePerson(responsiblePerson);
        return JsonUtils.objectTOJSONString(byResponsiblePerson);
    }

    //@RequiresPermissions("venues:get")
    @GetMapping("/find")
    public RespPageBean getVenuesByPage(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        String venuesName = (String)map.get("venuesName");
        String responsiblePerson = (String)map.get("responsiblePerson");
        String religiousSect = (String)map.get("religiousSect");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);
        return rmVenuesInfoService.getVenuesByPage(page,size,venuesName,responsiblePerson,religiousSect,token);
    }

    //场所更新（教职）
    @RequestMapping("/getVenueJz")
    public AppResponse queryVenuesJz(@RequestParam String search,@RequestHeader("token")String token) {
        return rmVenuesInfoService.queryVenuesJz(token,search);
    }
/*
    @RequestMapping("/getVenueAddress")
    public String getVenueAddress(@RequestParam String search) {
        List<VenuesEntity> list = rmVenuesInfoService.queryAll(search);
        return JsonUtils.objectTOJSONString(list);
    }*/

    //地图(app用)
    @RequestMapping("/map/getVenues")
    public AppResponse getMapVenues(@RequestParam String search,@RequestParam String religiousSect) {
        return rmVenuesInfoService.getMapVenues(search,religiousSect);
    }

    //地图场所详情（app用)
    @RequestMapping("/map/venuesDetail")
    public AppDetailRes getMapVenuesDetail(@RequestParam String venuesId) {
        AppDetailRes appResponse = rmVenuesInfoService.getMapVenuesDetail(venuesId);
        return appResponse;
    }

}
