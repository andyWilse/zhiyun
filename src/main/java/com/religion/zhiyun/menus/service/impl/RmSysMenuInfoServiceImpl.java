package com.religion.zhiyun.menus.service.impl;

import com.religion.zhiyun.menus.dao.MenuInfoMapper;
import com.religion.zhiyun.menus.entity.MenuEntity;
import com.religion.zhiyun.menus.entity.MenuList;
import com.religion.zhiyun.menus.entity.MenuRespons;
import com.religion.zhiyun.menus.service.RmSysMenuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RmSysMenuInfoServiceImpl implements RmSysMenuInfoService {

    @Autowired
    private MenuInfoMapper rmSysMenuInfoMapper;
    @Override
    public MenuList findAll() {
        MenuList  list=new MenuList();
        List<MenuEntity> allParents = rmSysMenuInfoMapper.findAllParents();
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
        list.setParent(rmSysMenuInfoMapper.findRoot().toArray());

        return list;
    }
}
