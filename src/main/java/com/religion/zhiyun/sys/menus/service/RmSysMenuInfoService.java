package com.religion.zhiyun.sys.menus.service;

import com.religion.zhiyun.sys.menus.entity.MenuList;
import com.religion.zhiyun.sys.menus.entity.RespPage;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.Map;

public interface RmSysMenuInfoService {
    //查询
    RespPage findAll();

    //查询
    RespPageBean findTreeMenus();

    //保存
    RespPageBean saveRoleMenus(Map<String,String> map);

    //获取角色下所有菜单
    MenuList getMenuByRole(String roleId);

    //保存
    RespPageBean userGrand(Map<String,String> map);

    //获取角色下所有菜单
    MenuList getMenuByUser(String roleId);


}
