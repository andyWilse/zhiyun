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
    public RespPageBean getTreeMenus(@RequestHeader("token")String token){
        return rmSysMenuInfoService.findTreeMenus(token);
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
    public RespPageBean userGrand(@RequestBody Map<String,String> map,@RequestHeader("token")String token){
        return rmSysMenuInfoService.userGrand(map,token);
    }

    @GetMapping("/getByUser/{userId}")
    public PageResponse getMenuByUser(@PathVariable String userId){
        return rmSysMenuInfoService.getMenuByUser(userId);
    }

    @GetMapping("/getGrand/{menuId}")
    public PageResponse getGrandByMenu(@RequestHeader("token")String token,@PathVariable ("menuId")String menuId){
        return rmSysMenuInfoService.getGrandByMenu(token,menuId);
    }

    @PostMapping("/roleGrand")
    public PageResponse roleGrand(){
        return rmSysMenuInfoService.roleGrand();
    }

}
