package com.religion.zhiyun.venues.controller;

import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.PcResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import com.religion.zhiyun.venues.entity.VenuesManagerEntity;
import com.religion.zhiyun.venues.services.VenuesManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/manager")
public class VenuesManagerController {

    @Autowired
    private VenuesManagerService venuesManagerService;


    @GetMapping("/findManager")
    public PageResponse findManager(@RequestParam Map<String, Object> map, @RequestHeader("token")String token) {
        return venuesManagerService.findManager(map,token);
    }

    @PostMapping("/add")
    public PageResponse add(@RequestBody VenuesManagerEntity venuesManagerEntity, @RequestHeader("token")String token) {
        return venuesManagerService.add(venuesManagerEntity,token);
    }

    @PostMapping("/updateManager")
    public PageResponse updateManager(@RequestBody VenuesManagerEntity venuesManagerEntity, @RequestHeader("token")String token) {
        return venuesManagerService.updateManager(venuesManagerEntity,token);
    }

    @PostMapping("/delete/{managerId}")
    public PageResponse deleteManager(@PathVariable int managerId, @RequestHeader("token")String token) {
        return venuesManagerService.delete(managerId,token);
    }

    @GetMapping("/getByManagerId")
    public PageResponse getByManagerId(@RequestParam String managerId) {
        return venuesManagerService.getByManagerId(managerId);
    }


}
