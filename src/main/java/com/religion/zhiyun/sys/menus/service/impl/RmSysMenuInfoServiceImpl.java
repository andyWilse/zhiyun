package com.religion.zhiyun.sys.menus.service.impl;

import com.religion.zhiyun.sys.menus.dao.MenuInfoMapper;
import com.religion.zhiyun.sys.menus.dao.RolePesnMapper;
import com.religion.zhiyun.sys.menus.entity.MenuEntity;
import com.religion.zhiyun.sys.menus.entity.MenuList;
import com.religion.zhiyun.sys.menus.entity.MenuRespons;
import com.religion.zhiyun.sys.menus.entity.PesnEntity;
import com.religion.zhiyun.sys.menus.service.RmSysMenuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RmSysMenuInfoServiceImpl implements RmSysMenuInfoService {

    @Autowired
    private MenuInfoMapper rmSysMenuInfoMapper;
    @Autowired
    private RolePesnMapper rolePesnMapper;


    @Override
    public MenuList findAll() {
        MenuList  list=new MenuList();
        String resourceType="01";
        List<MenuEntity> allParents = rmSysMenuInfoMapper.findMenus(resourceType);
        List<MenuRespons> childList=new ArrayList();
        if(null!=allParents && allParents.size()>0){
            for(int i=0;i<allParents.size();i++){
                MenuRespons respons=new MenuRespons();
                MenuEntity menuEntity = allParents.get(i);
                String menuPrtIds = menuEntity.getMenuPrtIds();
                List<MenuEntity> allChilds=new ArrayList<>();
                if(null!=menuPrtIds && ""!=menuPrtIds){
                    allChilds = rmSysMenuInfoMapper.findAllChilds(menuPrtIds.split(","));
                }
                respons.setMenuId(menuEntity.getMenuId());
                respons.setMenuNm(menuEntity.getMenuNm());
                respons.setChilds(allChilds.toArray());
                childList.add(respons);
            }
        }
        list.setChildren(childList.toArray());
        list.setParent(rmSysMenuInfoMapper.findMenus("02").toArray());

        return list;
    }

    @Override
    public MenuList findTreeMenus() {

        MenuList  list=new MenuList();
        //select选择框选项
        List<MenuEntity> allParents = rmSysMenuInfoMapper.findMenus("");
        list.setTypeOption(allParents.toArray());

        return list;
    }

    @Override
    public void saveRoleMenus(Map<String, String> map) {
        String roleId = map.get("roleId");
        String menus = map.get("menus");
        String[] split = menus.split(",");
        if(null!=split && split.length>0){
            rolePesnMapper.delete(roleId);
            for (String pmsnCd : split) {
                rolePesnMapper.add(pmsnCd,roleId);
            }
        }
    }

    @Override
    public MenuList getMenuByRole(String roleId) {
        MenuList menu=new MenuList();
        List<String> menuByRole = rolePesnMapper.getMenuByRole(roleId);
        if(null!=menuByRole && menuByRole.size()>0){
            menu.setParent(menuByRole.toArray());
        }
        return menu;
    }
}
