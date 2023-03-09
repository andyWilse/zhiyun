package com.religion.zhiyun.venues.controller;

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


    @PostMapping("/getManager")
    @ResponseBody
    public PcResponse getManager(@RequestParam Map<String, Object> map, @RequestHeader("token")String token) {
        String search = (String) map.get("search");
        return venuesManagerService.getManager(search,token);
    }

    @PostMapping("/add")
    @ResponseBody
    public RespPageBean add(@RequestBody VenuesManagerEntity venuesManagerEntity, @RequestHeader("token")String token) {
        return venuesManagerService.add(venuesManagerEntity,token);
    }


}
