package com.religion.zhiyun.menus.controller;

import com.religion.zhiyun.menus.entity.MenuList;
import com.religion.zhiyun.menus.service.RmSysMenuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
public class RmSysMenuInfoController {

    @Autowired
    private RmSysMenuInfoService rmSysMenuInfoService;

    @GetMapping("/find")
    public MenuList getEmpByPage(){
        return rmSysMenuInfoService.findAll();
    }

}
