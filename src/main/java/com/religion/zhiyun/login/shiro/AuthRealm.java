package com.religion.zhiyun.login.shiro;

import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.user.entity.RoleEntity;
import com.religion.zhiyun.user.entity.SysPermission;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.service.RmSysRolePermService;
import com.religion.zhiyun.user.service.SysRoleService;
import com.religion.zhiyun.user.service.SysUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class AuthRealm extends AuthorizingRealm {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RmSysRolePermService rmSysRolePermService;

    @Autowired
    private SysRoleService sysRoleService;
    @Resource
    private RmStaffInfoMapper rmStaffInfoMapper;

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取前端传来的token
        String accessToken= (String) token.getPrincipal();

        //redis缓存中这样存值， key为token，value为username
        //根据token去缓存里查找用户名
        String username=stringRedisTemplate.opsForValue().get(accessToken);
        if(username==null){
            //查找的用户名为空，即为token失效
            throw new IncorrectCredentialsException("token失效，请重新登录");
        }
        //此方法需要返回一个AuthenticationInfo类型的数据
        // 因此返回一个它的实现类SimpleAuthenticationInfo,将user以及获取到的token传入它可以实现自动认证
        SimpleAuthenticationInfo simpleAuthenticationInfo=null;
        SysUserEntity user = sysUserService.queryByName(username);
        List<Map<String,Object>> manager= rmStaffInfoMapper.getByTel(username);//教职人员
        if(null!=user && null!=manager && manager.size()>0){
            throw new RuntimeException("用户身份重复，请联系管理员！");
        }else if(user==null && 0==manager.size()){
            throw new UnknownAccountException("用户不存在!");
        }else if(null!=user){
            simpleAuthenticationInfo=new SimpleAuthenticationInfo(user,accessToken,"");
        }else if(null!=manager && manager.size()>0){
            simpleAuthenticationInfo=new SimpleAuthenticationInfo(manager.get(0),accessToken,"");
        }

        return simpleAuthenticationInfo;
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //从认证那里获取到用户对象User
        SysUserEntity user = (SysUserEntity) principals.getPrimaryPrincipal();

        //此方法需要一个AuthorizationInfo类型的返回值，因此返回一个它的实现类SimpleAuthorizationInfo
        //通过SimpleAuthorizationInfo里的addStringPermission()设置用户的权限
        SimpleAuthorizationInfo simpleAuthorizationInfo=new SimpleAuthorizationInfo();
        //simpleAuthorizationInfo.addStringPermission(user.get);

        /*List<RoleEntity> roles = sysRoleService.selectRoleByUserId(user.getUserId());
        if(roles!=null && roles.size()>0){
            for (RoleEntity role : roles) {
                authorizationInfo.addRole(role.getRoleId()+"");//角色存储
                rol=role.getRoleId();
            }
        }*/
        int rol=0;
        try {
            List<RoleEntity> roles = sysRoleService.selectRoleByUserId(user.getUserId());
            if(roles!=null && roles.size()>0){
                for (RoleEntity role : roles) {
                    simpleAuthorizationInfo.addRole(role.getRoleId()+"");//角色存储
                    rol=role.getRoleId();
                }
            }
            //此处如果多个角色都拥有某项权限，bu会数据重复，内部用的是Set
            List<SysPermission> sysPermissions = rmSysRolePermService.getRolePerm(String.valueOf(rol));
            if(null!=sysPermissions && sysPermissions.size()>0){
                for (SysPermission perm : sysPermissions) {
                    simpleAuthorizationInfo.addStringPermission(perm.getPermPrtCd());//权限存储
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return simpleAuthorizationInfo;
    }

}
