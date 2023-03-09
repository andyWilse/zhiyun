package com.religion.zhiyun.sys.menus.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.menus.dao.MenuInfoMapper;
import com.religion.zhiyun.sys.menus.dao.RolePesnMapper;
import com.religion.zhiyun.sys.menus.entity.MenuEntity;
import com.religion.zhiyun.sys.menus.entity.MenuList;
import com.religion.zhiyun.sys.menus.entity.MenuRespons;
import com.religion.zhiyun.sys.menus.entity.RespPage;
import com.religion.zhiyun.sys.menus.service.RmSysMenuInfoService;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RmSysMenuInfoServiceImpl implements RmSysMenuInfoService {

    @Autowired
    private MenuInfoMapper rmSysMenuInfoMapper;
    @Autowired
    private RolePesnMapper rolePesnMapper;


    @Override
    public RespPage findAll() {
        MenuList  list= null;
        long code = ResultCode.FAILED.getCode();
        String message= "菜单获取";
        try {
            list = new MenuList();
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
                    respons.setIconNm(menuEntity.getIconNm());
                    childList.add(respons);
                }
            }
            list.setChildren(childList.toArray());
            list.setParent(rmSysMenuInfoMapper.findMenus("02").toArray());

            code = ResultCode.SUCCESS.getCode();
            message= "菜单获取成功";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RespPage(code,message,list);
    }

    @Override
    public RespPageBean findTreeMenus() {

        List<Map<String, Object>> menusTree = null;
        long code = ResultCode.FAILED.getCode();
        String message= "菜单获取失败";
        try {
            MenuList  list=new MenuList();
            menusTree = new ArrayList<>();
            Map<String, Object> map=new HashMap<>();
            //获取一级菜单
            List<Map<String, Object>> oneTree = rmSysMenuInfoMapper.findOneTree();

            if(null!=oneTree && oneTree.size()>0){
                for (int i=0;i<oneTree.size();i++){
                    Map<String, Object> oneTreeMap = oneTree.get(i);
                    String type = (String) oneTreeMap.get("type");
                    int id = (int) oneTreeMap.get("id");
                    //根据一级菜单获取二级菜单
                    if(type.equals("01")){
                        List<Map<String, Object>> twoTree = rmSysMenuInfoMapper.findTwoTree(id);
                        if(null!=twoTree && twoTree.size()>0) {
                            for (int j = 0; j < twoTree.size(); j++) {
                                Map<String, Object> twoTreeMap = twoTree.get(j);
                                String twoTreeType = (String) twoTreeMap.get("type");
                                int twoTreeId = (int) twoTreeMap.get("id");
                                //根据二级菜单获取按钮
                                if(twoTreeType.equals("03")){
                                    List<Map<String, Object>> buttonTree = rmSysMenuInfoMapper.findButtonTree(twoTreeId);
                                    twoTreeMap.put("children",buttonTree.toArray());
                                }
                            }
                        }
                        oneTreeMap.put("children",twoTree.toArray());
                    }

                }
            }
            map.put("id","1");
            map.put("name","全部菜单");
            map.put("children",oneTree.toArray());
            menusTree.add(map);
            System.out.println(menusTree.toArray());
            //select选择框选项
            List<MenuEntity> allParents = rmSysMenuInfoMapper.findMenus("");
            list.setTypeOption(allParents.toArray());
            System.out.println(allParents.toArray());
            code = ResultCode.SUCCESS.getCode();
            message= "菜单获取成功";
        } catch (Exception e) {
            code = ResultCode.FAILED.getCode();
            message= "菜单获取失败";
            e.printStackTrace();
        }

        return new RespPageBean(code,message,menusTree.toArray());
    }

    @Override
    @Transactional
    public RespPageBean saveRoleMenus(Map<String, String> map) {
        long code= ResultCode.FAILED.getCode();
        String message= "权限修改";
        try {

            String roleId = map.get("roleId");
            String menus = map.get("menus");
            String[] split = menus.split(",");
            //删除原数据
            long delete = rolePesnMapper.delete(roleId);
            //新增数据
            if(null!=split && split.length>0){
                for (String pmsnCd : split) {
                    rolePesnMapper.add(pmsnCd,roleId);
                }
            }
            code = ResultCode.SUCCESS.getCode();
            message = "权限修改成功";
        } catch (Exception e) {
            message = "权限修改失败";
            e.printStackTrace();
        }
        return new RespPageBean(code,message);
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

    @Override
    @Transactional
    public RespPageBean userGrand(Map<String, String> map) {
        long code= ResultCode.FAILED.getCode();
        String message= "用户权限修改";
        try {
            String userId = map.get("userId");
            String menus = map.get("menus");
            String[] split = menus.split(",");
            //删除原数据
            long delete = rolePesnMapper.deleteUserGrand(userId);
            //新增数据
            if(null!=split && split.length>0){
                for (String postCd : split) {
                    rolePesnMapper.addUserGrand(postCd,userId);
                }
            }
            code = ResultCode.SUCCESS.getCode();
            message = "用户权限修改成功";
        } catch (Exception e) {
            message = "用户权限修改失败";
            e.printStackTrace();
        }
        return new RespPageBean(code,message);
    }

    @Override
    public MenuList getMenuByUser(String userId) {
        MenuList menu=new MenuList();
        List<String> menuByRole = rolePesnMapper.getMenuByUser(userId);
        if(null!=menuByRole && menuByRole.size()>0){
            menu.setParent(menuByRole.toArray());
        }
        return menu;
    }
}
