package com.religion.zhiyun.sys.menus.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.sys.menus.dao.MenuInfoMapper;
import com.religion.zhiyun.sys.menus.dao.RolePesnMapper;
import com.religion.zhiyun.sys.menus.entity.MenuEntity;
import com.religion.zhiyun.sys.menus.entity.MenuList;
import com.religion.zhiyun.sys.menus.entity.MenuRespons;
import com.religion.zhiyun.sys.menus.entity.RespPage;
import com.religion.zhiyun.sys.menus.service.RmSysMenuInfoService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.enums.OperaEnums;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RmSysMenuInfoServiceImpl implements RmSysMenuInfoService {

    @Autowired
    private MenuInfoMapper rmSysMenuInfoMapper;
    @Autowired
    private RolePesnMapper rolePesnMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private OperateRecordService operateRecordService;


    public RmSysMenuInfoServiceImpl() {
    }

    @Override
    public RespPage findAll(String token) {
        MenuList  menuList = new MenuList();
        long code = ResultCode.FAILED.getCode();
        String message= "菜单获取";
        try {
            //获取用户
            String login = this.getLogin(token);
            List<SysUserEntity> sysUserList = sysUserMapper.queryByTel(login);
            if(null==sysUserList || sysUserList.size()<1){
                throw new RuntimeException("用户信息丢失！");
            }
            int userId = sysUserList.get(0).getUserId();

            //获取菜单
            List<MenuEntity> parentMenus =new ArrayList<>();
            List<MenuEntity> buttonMenus=new ArrayList();
            List<MenuEntity> allMenus = rmSysMenuInfoMapper.findGrandMenus(String.valueOf(userId));
            if(null!=allMenus && allMenus.size()>0){
                for(int i=0;i<allMenus.size();i++){
                    MenuEntity menuEntity = allMenus.get(i);
                    String resourceType = menuEntity.getResourceType();
                    //如果为‘02’
                    if("02".equals(resourceType)){
                        parentMenus.add(menuEntity);
                    }else if("05".equals(resourceType)){
                        buttonMenus.add(menuEntity);
                    }
                }
            }

            //菜单
            List<MenuRespons> childMenus=new ArrayList();
            List<Map<String, Object>> grandParent = rmSysMenuInfoMapper.findGrandParent(String.valueOf(userId));
            if(null!=grandParent && grandParent.size()>0){
                for(int i=0;i<grandParent.size();i++){
                    MenuRespons respons=new MenuRespons();
                    Map<String, Object> map = grandParent.get(i);
                    Integer parentId = (Integer) map.get("parentId");
                    String menuIds = (String) map.get("menuIds");
                    String menuNm = (String) map.get("menuNm");
                    String iconNm = (String) map.get("iconNm");
                    List<MenuEntity> allChilds=new ArrayList<>();
                    if(null!=parentId && null!=menuIds && parentId!=0 && !menuIds.isEmpty()){
                        allChilds = rmSysMenuInfoMapper.findAllChilds(menuIds.split(","));
                    }
                    respons.setMenuId(parentId);
                    respons.setMenuNm(menuNm);
                    respons.setIconNm(iconNm);
                    respons.setChilds(allChilds.toArray());
                    childMenus.add(respons);
                }
            }
            //封装
            menuList.setChildren(childMenus.toArray());
            menuList.setParent(parentMenus.toArray());
            menuList.setButton(buttonMenus.toArray());

            code = ResultCode.SUCCESS.getCode();
            message= "菜单获取成功";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RespPage(code,message,menuList);
    }

    public MenuRespons getRespon(){
        MenuRespons respons=new MenuRespons();
       /* respons.setMenuId(menuEntity.getMenuId());
        respons.setMenuNm(menuEntity.getMenuNm());
        *//*String menuPrtIds = menuEntity.getMenuPrtIds();
        if(null!=menuPrtIds && ""!=menuPrtIds){
            allChilds = rmSysMenuInfoMapper.findAllChilds(menuPrtIds.split(","));
        }*//*
        respons.setChilds(allChilds.toArray());
        respons.setIconNm(menuEntity.getIconNm());*/
        return respons;
    }
    @Override
    public RespPageBean findTreeMenus(String token) {

        List<Map<String, Object>> menusTree = null;
        long code = ResultCode.FAILED.getCode();
        String message= "菜单获取失败";
        try {
            MenuList  list=new MenuList();
            menusTree = new ArrayList<>();
            Map<String, Object> map=new HashMap<>();
            List<Map<String, Object>> oneTree =new ArrayList<>();
            String login = this.getLogin(token);
            List<SysUserEntity> sysUserEntities = sysUserMapper.queryByTel(login);
            if(sysUserEntities.size()!=1){
                throw new RuntimeException(login+"不存在或存在多个用户，请联系管理员！");
            }

            String userId = String.valueOf(sysUserEntities.get(0).getUserId());

            //获取一级菜单
            oneTree = rmSysMenuInfoMapper.findOneTree(userId);
            if(null!=oneTree && oneTree.size()>0){
                for (int i=0;i<oneTree.size();i++){
                    Map<String, Object> oneTreeMap = oneTree.get(i);
                    String type = (String) oneTreeMap.get("type");
                    int id = (int) oneTreeMap.get("id");
                    //根据一级菜单获取二级菜单
                    if(type.equals("01")){
                        List<Map<String, Object>> twoTree = rmSysMenuInfoMapper.findTwoTree(id,userId);
                        if(null!=twoTree && twoTree.size()>0) {
                            for (int j = 0; j < twoTree.size(); j++) {
                                Map<String, Object> twoTreeMap = twoTree.get(j);
                                String twoTreeType = (String) twoTreeMap.get("type");
                                int twoTreeId = (int) twoTreeMap.get("id");
                                //根据二级菜单获取按钮
                                if(twoTreeType.equals("03")){
                                    List<Map<String, Object>> buttonTree = rmSysMenuInfoMapper.findButtonTree(twoTreeId,userId);
                                    twoTreeMap.put("children",buttonTree.toArray());
                                }
                            }
                        }
                        oneTreeMap.put("children",twoTree.toArray());
                    }
                    //客户管理特殊处理
                    if(id==1002){
                        List<Map<String, Object>> twoTree = rmSysMenuInfoMapper.findButtonTree(10020001,userId);
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
    public PageResponse getMenuByRole(String roleId) {
        long code= ResultCode.FAILED.getCode();
        String message= "角色权限获取回显";
        List<String> menuByRole=new ArrayList<>();

        try {
            menuByRole = rolePesnMapper.getMenuByRole(roleId);
            code= ResultCode.SUCCESS.getCode();
            message= "用户权限获取回显成功";
        } catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        return new PageResponse(code,message,menuByRole.toArray());
    }

    @Override
    @Transactional
    public RespPageBean userGrand(Map<String, String> map,String token) {
        long code= ResultCode.FAILED.getCode();
        String message= "用户权限修改";
        try {
            String userId = map.get("userId");
            String userNm = map.get("userNm");
            String menus = map.get("menus");
            String[] split = menus.split(",");
            //删除原数据
            long delete = rolePesnMapper.deleteUserGrand(userId);
            //新增数据
            if(null!=split && split.length>0){
                for (String postCd : split) {
                    if(!"1".equals(postCd)){
                        rolePesnMapper.addUserGrand(postCd,userId);
                    }
                }
            }

            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            String login = this.getLogin(token);
            vuMap.put("operator",login);
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",userId);
            vuMap.put("operateType", OperaEnums.user_grand_update.getCode());
            vuMap.put("operateContent", JsonUtils.arrToJSON(split));
            String operateDetail="用户："+userNm;
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            code = ResultCode.SUCCESS.getCode();
            message = "用户权限修改成功";
        } catch (Exception e) {
            message = "用户权限修改失败";
            e.printStackTrace();
        }
        return new RespPageBean(code,message);
    }

    @Override
    public PageResponse getMenuByUser(String userId) {
        long code= ResultCode.FAILED.getCode();
        String message= "用户权限获取回显";
        List<String> menuByUser=new ArrayList<>();
        try {
            menuByUser = rolePesnMapper.getMenuByUser(userId);
            code= ResultCode.SUCCESS.getCode();
            message= "用户权限获取回显成功";
        } catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        return new PageResponse(code,message,menuByUser.toArray());
    }

    @Override
    public PageResponse getGrandByMenu(String token, String menuId) {
        String login = this.getLogin(token);
        return null;
    }

    @Override
    public PageResponse roleGrand() {
        long code= ResultCode.FAILED.getCode();
        String message= "角色权限初始化失败！";

        try {
             /* String menus = menuInfoMapper.getMenus();
        System.out.println(menus);
*/
            ParamsVo vo=new ParamsVo();
            vo.setPage(0);
            vo.setSize(100000);
            List<SysUserEntity> usersByPage = sysUserMapper.getUsersByPage(vo);
            String qu="1001,1002,1003,1004,1006,1007,1008,10010001,10010002,10010003,10030002,10030003,10060001,10060002,10060003,1001000101,1001000102,1001000103,1001000201,1001000301,1001000302,1001000303,1002000101,1002000102,1002000103,1002000104,1002000105,1007000101,1007000102,1007000103";
            String jie="1001,1002,1003,1004,1006,10010001,10010002,10010003,10030002,10030003,10060001,10060002,10060003,1001000101,1001000102,1001000103,1001000201,1001000301,1001000302,1001000303,1002000101,1002000102,1002000103,1002000104,1002000105";
            String zhu="1001,1003,1004,10010001,10010002,10010003,10030002,10030003,1001000101,1001000102,1001000103,1001000201,1001000301,1001000302,1001000303";

            String[] split ={};
            for(int i=0;i<usersByPage.size();i++){
                SysUserEntity sy = usersByPage.get(i);

                int userId = sy.getUserId();
                String identity= sy.getIdentity();
                if(identity.equals("10000002") || identity.equals("10000003")){
                    split =qu.split(",");
                }else if(identity.equals("10000004") || identity.equals("10000005")){
                    split =jie.split(",");
                }else if(identity.equals("10000006") || identity.equals("10000007")){
                    split =zhu.split(",");
                }

                if(null!=split && split.length>0){
                    long delete = rolePesnMapper.deleteUserGrand(String.valueOf(userId));
                    //新增数据
                    if(null!=split && split.length>0){
                        for (String postCd : split) {
                            rolePesnMapper.addUserGrand(postCd,String.valueOf(userId));
                        }
                    }
                }
            }

            code= ResultCode.SUCCESS.getCode();
            message= "角色权限初始化成功";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResponse(code,message);
    }

    /**
     * 获取登录人
     * @return
     */
    public String getLogin(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("登录过期，请重新登陆！");
        }
        return loginNm;
    }

    /**
     * 获取
     * @return
     */
    public ParamsVo getAuth(String token){
        String login = this.getLogin(token);
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
        String area="";
        String town ="";
        String relVenuesId="";
        String[] venuesArr={};
        if(null!=sysUserEntity){
            relVenuesId = sysUserEntity.getRelVenuesId();
            town = sysUserEntity.getTown();
            area = sysUserEntity.getArea();
        }else{
            throw new RuntimeException("用户已过期，请重新登录！");
        }
        ParamsVo vo=new ParamsVo();
        vo.setArea(area);
        vo.setTown(town);
        vo.setVenues(relVenuesId);
        if(null!=relVenuesId && !relVenuesId.isEmpty()){
            venuesArr=relVenuesId.split(",");
            vo.setVenuesArr(venuesArr);
        }
        return vo;

    }
}
