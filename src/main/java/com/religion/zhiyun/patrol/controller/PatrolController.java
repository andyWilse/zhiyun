package com.religion.zhiyun.patrol.controller;

import com.religion.zhiyun.patrol.service.PatrolService;
import com.religion.zhiyun.utils.response.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/patrol")
public class PatrolController {

    @Autowired
    private PatrolService patrolService;

    @RequestMapping(value = "/ouHai")
    public AppResponse getOuHai() {
        return patrolService.getOuHai();
    }

    @RequestMapping(value = "/town")
    public AppResponse getTown() {
        return patrolService.getTown();
    }

    @RequestMapping(value = "/detail")
    public AppResponse getVenuesDetail(@RequestParam Map<String,Object> map) {
        return patrolService.getVenuesDetail(map);
    }

    @RequestMapping(value = "/townSummary")
    public AppResponse getTownSummary(@RequestParam Map<String,Object> map) {
        return patrolService.getTownSummary(map);
    }

    @RequestMapping(value = "/venuesItems")
    public AppResponse getVenuesItems(@RequestParam Map<String,Object> map) {
        return patrolService.getVenuesItems(map);
    }

    @RequestMapping(value = "/venuesRank/{type}")
    public AppResponse getVenuesRank(@PathVariable("type") String type) {
        return patrolService.getVenuesRank(type);
    }

    @RequestMapping(value = "/townRank")
    public AppResponse getTownRank() {
        return patrolService.getTownRank();
    }

}
