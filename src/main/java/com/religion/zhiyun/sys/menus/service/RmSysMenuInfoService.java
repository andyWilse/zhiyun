package com.religion.zhiyun.sys.menus.service;

import com.religion.zhiyun.sys.menus.entity.MenuList;
import com.religion.zhiyun.sys.menus.entity.RespPage;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.Map;

public interface RmSysMenuInfoService {
    //查询
    RespPage findAll(String token);

    //查询
    RespPageBean findTreeMenus(String token);

    //保存
    RespPageBean saveRoleMenus(Map<String,String> map);

    //获取角色下所有菜单
    PageResponse getMenuByRole(String roleId);

    //保存
    RespPageBean userGrand(Map<String,String> map,String token);

    //获取角色下所有菜单
    PageResponse getMenuByUser(String roleId);

    PageResponse getGrandByMenu(String token,String menuId);


    PageResponse roleGrand();


}
