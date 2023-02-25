/*
package com.religion.zhiyun.user.shiro;

import com.religion.zhiyun.user.entity.RoleEntity;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.service.SysRoleService;
import com.religion.zhiyun.user.service.SysUserService;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.List;

*/
/*
 * Created by WJ on 2019/3/28 0028
 * 自定义权限匹配和密码匹配**//*



public class MyShiroRealm extends AuthorizingRealm {
    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysUserService sysUserService;
    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        SysUserEntity User = (SysUserEntity) principals.getPrimaryPrincipal();
        try {
            List<RoleEntity> roles = sysRoleService.selectRoleByUserId(User.getUserId());
            for (RoleEntity role : roles) {
                authorizationInfo.addRole(role.getRoleId()+"");//角色存储
            }
            //此处如果多个角色都拥有某项权限，bu会数据重复，内部用的是Set
*/
/*List<SysPermission> sysPermissions = sysPermissionService.selectPermByRole(roles);
            for (SysPermission perm : sysPermissions) {
                authorizationInfo.addStringPermission(perm.getPermission());//权限存储
            }*//*


        } catch (Exception e) {
            e.printStackTrace();
        }
        return authorizationInfo;
    }

//主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();
//       System.out.println(token.getCredentials());
        //通过username从数据库中查找 User对象，如果找到，没找到.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        //User user = userRepository.findByUsername(username).get();//*

        SysUserEntity user = sysUserService.queryByName(username);
        if (user == null) {
            return null;
        }

        if (user.getValidInd() .equals("0")) { //账户冻结
            throw new LockedAccountException();
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user, //用户名
                user.getPasswords(), //密码
                ByteSource.Util.bytes(user.getCredentialsSalt()),//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }
}

*/
