package com.religion.zhiyun.sys.menus.controller;

import com.religion.zhiyun.sys.menus.entity.MenuList;
import com.religion.zhiyun.sys.menus.entity.RespPage;
import com.religion.zhiyun.sys.menus.service.RmSysMenuInfoService;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
    //@RequiresPermissions("menu:find")
    public RespPage getMenuByPage(@RequestHeader("token")String token){
        return rmSysMenuInfoService.findAll(token);
    }

    @PostMapping("/save")
    public RespPageBean saveMenus(@RequestBody Map<String,String> map){
        return rmSysMenuInfoService.saveRoleMenus(map);
    }

    @GetMapping("/getByrole/{roleId}")
    public PageResponse getMenuByRole(@PathVariable String roleId){
        return rmSysMenuInfoService.getMenuByRole(roleId);
    }

    @PostMapping("/userGrand")
    public RespPageBean userGrand(@RequestBody Map<String,String> map){
        return rmSysMenuInfoService.userGrand(map);
    }

    @GetMapping("/getByUser/{userId}")
    public PageResponse getMenuByUser(@PathVariable String userId){
        return rmSysMenuInfoService.getMenuByUser(userId);
    }


}
