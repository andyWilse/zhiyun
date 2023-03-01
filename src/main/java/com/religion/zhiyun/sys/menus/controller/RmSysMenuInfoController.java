package com.religion.zhiyun.sys.menus.controller;

import com.religion.zhiyun.sys.menus.entity.MenuList;
import com.religion.zhiyun.sys.menus.service.RmSysMenuInfoService;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/menu")
public class RmSysMenuInfoController {

    @Autowired
    private RmSysMenuInfoService rmSysMenuInfoService;

    @GetMapping("/getTreeMenus")
    public RespPageBean getTreeMenus(){
        return rmSysMenuInfoService.findTreeMenus();
    }

    @GetMapping("/find")
    public MenuList getMenuByPage(){
        return rmSysMenuInfoService.findAll();
    }

    @PostMapping("/save")
    public RespPageBean saveMenus(@RequestBody Map<String,String> map){
        return rmSysMenuInfoService.saveRoleMenus(map);
    }

    @GetMapping("/getByrole/{roleId}")
    public MenuList getMenuByRole(@PathVariable String roleId){
        return rmSysMenuInfoService.getMenuByRole(roleId);
    }

}
