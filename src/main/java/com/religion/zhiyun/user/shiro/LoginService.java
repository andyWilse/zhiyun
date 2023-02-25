package com.religion.zhiyun.user.shiro;

import com.religion.zhiyun.user.entity.RoleEntity;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.service.SysRoleService;
import com.religion.zhiyun.user.service.SysUserService;
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

    public LoginInfo getLoginInfo(String username) {
        //User user = userRepository.findByUsername(username).get();
        SysUserEntity user = sysUserService.queryByName(username);
        //List<SysRole> roles = sysRoleService.selectRoleByUserId(user.getId());
        List<RoleEntity> roles = sysRoleService.selectRoleByUserId(user.getUserId());

        Set<String> roleList = new HashSet<>();
        Set<String> permissionList = new HashSet<>();
        for (RoleEntity role : roles) {
            roleList.add(role.getRoleId()+"");//角色存储
        }
        //此处如果多个角色都拥有某项权限，bu会数据重复，内部用的是Set
        /*List<SysPermission> sysPermissions = sysPermissionService.selectPermByRole(roles);
        for (SysPermission perm : sysPermissions) {
            permissionList.add(perm.getPermission());//权限存储
        }*/

        return  new LoginInfo(roleList,permissionList);
    }
}

