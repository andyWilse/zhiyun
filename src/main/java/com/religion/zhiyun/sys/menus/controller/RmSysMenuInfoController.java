package com.religion.zhiyun.sys.menus.controller;

import com.religion.zhiyun.sys.menus.entity.MenuList;
import com.religion.zhiyun.sys.menus.service.RmSysMenuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/menu")
public class RmSysMenuInfoController {

    @Autowired
    private RmSysMenuInfoService rmSysMenuInfoService;

    @GetMapping("/find")
    public MenuList getMenuByPage(){
        return rmSysMenuInfoService.findAll();
    }

    @PostMapping("/save")
    public void saveMenus(@RequestBody Map<String,String> map){
        rmSysMenuInfoService.saveRoleMenus(map);
    }

    @GetMapping("/getByrole/{roleId}")
    public MenuList getMenuByRole(@PathVariable String roleId){

        return rmSysMenuInfoService.getMenuByRole(roleId);
    }

}
