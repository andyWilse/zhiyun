package com.religion.zhiyun.login.service;

import com.religion.zhiyun.login.entity.LoginInfo;
import com.religion.zhiyun.user.entity.RoleEntity;
import com.religion.zhiyun.user.entity.SysPermission;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.service.RmSysRolePermService;
import com.religion.zhiyun.user.service.SysRoleService;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.utils.redis.AppRedisCacheManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LoginService {
    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private RmSysRolePermService rmSysRolePermService;

    @Autowired
    private AppRedisCacheManager appRedisCacheManager;

    public LoginInfo getLoginInfo(String username) {
        //User user = userRepository.findByUsername(username).get();
        SysUserEntity user = sysUserService.queryByName(username);
        //List<SysRole> roles = sysRoleService.selectRoleByUserId(user.getId());
        List<RoleEntity> roles = sysRoleService.selectRoleByUserId(user.getUserId());
        int rol=0;
        Set<String> roleList = new HashSet<>();
        Set<String> permissionList = new HashSet<>();
        for (RoleEntity role : roles) {
            roleList.add(role.getRoleId()+"");//角色存储
            rol=role.getRoleId();
        }
        //此处如果多个角色都拥有某项权限，bu会数据重复，内部用的是Set
        List<SysPermission> sysPermissions = rmSysRolePermService.getRolePerm(String.valueOf(rol));
        if(null!=sysPermissions && sysPermissions.size()>0){
            for (SysPermission perm : sysPermissions) {
                permissionList.add(perm.getPermPrtCd());//权限存储
            }
        }

        //此处如果多个角色都拥有某项权限，bu会数据重复，内部用的是Set
        /*List<SysPermission> sysPermissions = sysPermissionService.selectPermByRole(roles);
        for (SysPermission perm : sysPermissions) {
            permissionList.add(perm.getPermission());//权限存储
        }*/
        if(null==user){
            throw new RuntimeException("用户信息丢失");
        }
        String key=user.getLoginNm();
        //设置redis
        Subject currentUser = SecurityUtils.getSubject();
        // 将认证码存入redis，有效时长5分钟
        //appRedisCacheManager.setUnion(key,currentUser,5L, TimeUnit.MINUTES);
        return  new LoginInfo(roleList,permissionList);
    }
}

