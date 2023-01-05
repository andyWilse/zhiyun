package com.religion.zhiyun.staff.controller;

import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/staff")
public class RmStaffInfoController {

    @Autowired
    private RmStaffInfoService rmStaffInfoService;

    @PostMapping("/add")
    @ResponseBody
    public String add(@RequestBody String staffJson) {
        StaffEntity staffEntity = JsonUtils.jsonTOBean(staffJson, StaffEntity.class);
        rmStaffInfoService.add(staffEntity);
        return "添加成功！";
    }
}
