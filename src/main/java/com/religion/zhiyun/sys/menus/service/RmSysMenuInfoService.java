package com.religion.zhiyun.sys.menus.service;

import com.religion.zhiyun.sys.menus.entity.MenuList;
import java.util.Map;

public interface RmSysMenuInfoService {
    //查询
    MenuList findAll();

    //查询
    MenuList findTreeMenus();

    //保存
    void saveRoleMenus(Map<String,String> map);

    //获取角色下所有菜单
    MenuList getMenuByRole(String roleId);


}
