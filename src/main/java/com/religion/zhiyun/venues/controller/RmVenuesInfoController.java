package com.religion.zhiyun.venues.controller;


import com.religion.zhiyun.login.api.CommonResult;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
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
    public CommonResult updateVenues(@RequestBody VenuesEntity venuesEntity,@RequestHeader("token")String token) {
        rmVenuesInfoService.update(venuesEntity,token);
        return CommonResult.success("修改成功！");
    }

    @PostMapping("/delete/{venuesId}")
    public void delete(@PathVariable int venuesId,@RequestHeader("token")String token) {
        rmVenuesInfoService.delete(venuesId,token);
    }

    @RequestMapping("/querySelect")
    public RespPageBean querySelect(@RequestParam String search,@RequestParam String town) {
        return rmVenuesInfoService.querySelect(search,town);
    }

    //app下拉使用(监管)
    @RequestMapping("/getVenues")
    public AppResponse getVenuesList(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return rmVenuesInfoService.queryVenues(map,token);
    }

    //场所下拉使用(pc教职)
    @RequestMapping("/getStaffVenues")
    public AppResponse getStaffVenues(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return rmVenuesInfoService.queryStaffVenues(map,token);
    }

    //根据教派类别查询
    @RequestMapping("/querySectAll")
    public String querySectAll(String religiousSect) {
        List<VenuesEntity> list = rmVenuesInfoService.querySectAll(religiousSect);
        return JsonUtils.objectTOJSONString(list);
    }

    //根据id获取该教堂
    @RequestMapping("/getVenueByID")
    public AppResponse getVenueByID(String venuesId){
        return rmVenuesInfoService.getVenueByID(venuesId);
    }

    //统计场所数量（app首页）
    @RequestMapping("/getAllNum")
    public PageResponse getAllNum(@RequestHeader("token")String token){
        return rmVenuesInfoService.getAllNum(token);
    }

    //统计场所数量（app地图）
    @RequestMapping("/map/getVeNum")
    public PageResponse getVeNum(@RequestParam String type,@RequestHeader("token")String token){
        return rmVenuesInfoService.getVenueNum(type,token);
    }

    //统计场所弹框（app首页）
    @RequestMapping("/getDialogVenue")
    public PageResponse getDialogVenue(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        return rmVenuesInfoService.getDialogVenue(map,token);
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
        String venuesPhone = (String)map.get("venuesPhone");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);
        return rmVenuesInfoService.getVenuesByPage(page,size,venuesName,responsiblePerson,religiousSect,venuesPhone,token);
    }

    //场所更新：app下拉使用(管理)
    @RequestMapping("/getVenueJz")
    public AppResponse queryVenuesJz(@RequestParam String search,@RequestHeader("token")String token) {
        return rmVenuesInfoService.queryVenuesJz(token,search);
    }

    //地图(app用)
    @RequestMapping("/map/getVenues")
    public AppResponse getMapVenues(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return rmVenuesInfoService.getMapVenues(map,token);
    }

    //地图(pc用)
    @RequestMapping("/map/mapVenues")
    public AppResponse getMapsVenues(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return rmVenuesInfoService.getMapsVenues(map,token);
    }

    //地图场所详情（app用)
    @RequestMapping("/map/venuesDetail")
    public AppDetailRes getMapVenuesDetail(@RequestParam String venuesId,@RequestHeader("token")String token) {
        AppDetailRes appResponse = rmVenuesInfoService.getMapVenuesDetail(venuesId,token);
        return appResponse;
    }

    //地图分类列表（app
    @RequestMapping("/map/venuesType")
    public AppResponse getvenuesByType(@RequestParam String type) {
        AppResponse appResponse = rmVenuesInfoService.getvenuesByType(type);
        return appResponse;
    }

    //场所综合得分
    @RequestMapping("/daPing/score")
    public AppResponse getVenuesScore() {
        return rmVenuesInfoService.getVenuesScore();
    }

}
