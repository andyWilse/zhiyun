package com.religion.zhiyun.monitor.controller;


import com.religion.zhiyun.monitor.entity.MonitorEntity;
import com.religion.zhiyun.monitor.service.RmMonitroInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monitor")
public class RmMonitroInfoController {
    @Autowired
    private RmMonitroInfoService rmMonitroInfoService;

    @RequestMapping("/allMonitor")
    public String allMonitor(){
        List<MonitorEntity> list = rmMonitroInfoService.allMonitro();
        return JsonUtils.objectTOJSONString(list);
    }

    @ResponseBody
    @PostMapping("/addMonitro")
    public String addMonitro(@RequestBody String monitroJson){
        MonitorEntity monitroEntity = JsonUtils.jsonTOBean(monitroJson, MonitorEntity.class);
        rmMonitroInfoService.addMonitro(monitroEntity);
        return "添加成功！";
    }

    @PostMapping("/repair")
    public PageResponse moRepair(@RequestBody String repairJson){
        return rmMonitroInfoService.addMonitorEvent(repairJson);
    }

    @PostMapping("/deleteMonitro")
    public void deleteMonitro(){
        rmMonitroInfoService.deleteMonitro("monitroId");
    }

    //摄像头数量
    @RequestMapping("/getAllNum")
    public PageResponse getAllNum(@RequestHeader("token")String token){
        return rmMonitroInfoService.getAllNum(token);
    }

    /**
     * 根据摄像头状态查看摄像头信息
     * @param state
     * @return
     */
    @RequestMapping("/getMonitorByState")
    public String getMonitorByState(String state){
        List<MonitorEntity> list = rmMonitroInfoService.getMonitorByState(state);
        return JsonUtils.objectTOJSONString(list);
    }

    @RequestMapping("/getMonitorByVenuesId")//有问题
    public String getMonitorByVenuesId(String state){
        List<MonitorEntity> list = rmMonitroInfoService.getMonitorByVenuesId(state);
        return JsonUtils.objectTOJSONString(list);
    }

    @GetMapping("/findPage")
    public PageResponse getMonitorByPage(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        return rmMonitroInfoService.getMonitorByPage(map,token);
    }

    @RequestMapping("/getMonitorURLByAccessNum/{accessNum}")
    public String getMonitorURLByAccessNum(@PathVariable String accessNum){
        return rmMonitroInfoService.getMonitorURLByAccessNum(accessNum);

    }

    //app用
    @RequestMapping("/getVenuesMonitor")
    public RespPageBean getVenuesMonitor(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        String venuesName = (String) map.get("venuesName");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return rmMonitroInfoService.getVenuesMonitor(page,size,venuesName,token);
    }

    //监控设备查询(app监控)
    @RequestMapping("/getMonitors")
    public RespPageBean getMonitors(@RequestParam Map<String, Object> map,@RequestHeader("token")String token) {
        return rmMonitroInfoService.getMonitors(map,token);
    }

    //监控详情查询（地图）
    @GetMapping("/getMapMoDetail")
    public PageResponse getMapMoDetail(@RequestParam String venuesId) {
        return rmMonitroInfoService.getMoDetail(venuesId,"01");
    }

    //监控详情查询（教职）
    @GetMapping("/getJzMoDetail")
    public PageResponse getJzMoDetail(@RequestParam String accessNumber) {
        return rmMonitroInfoService.getMoDetail(accessNumber,"02");
    }

    @RequestMapping("/monitorReport")
    public AppResponse monitRepairReport(@RequestParam Map<String, Object> map, @RequestHeader("token")String token) {
        return rmMonitroInfoService.monitRepairReport(map,token);
    }

    @RequestMapping("/monitorHandle")
    public AppResponse monitRepairHandle(@RequestParam Map<String, Object> map, @RequestHeader("token")String token) {
        return rmMonitroInfoService.monitRepairHandle(map,token);
    }

    //监控详情查询（地图）
    @GetMapping("/getMoDaPing")
    public PageResponse getMoDaPing(@RequestParam String venuesName) {
        return rmMonitroInfoService.getMoDaPing(venuesName);
    }

}
