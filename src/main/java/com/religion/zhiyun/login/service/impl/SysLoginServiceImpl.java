package com.religion.zhiyun.login.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.RoleEntity;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.login.service.SysLoginService;

import com.religion.zhiyun.user.service.SysRoleService;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.login.entity.LoginInfo;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.redis.AppRedisCacheManager;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.sms.SendVerifyCode;
import com.religion.zhiyun.utils.sms.sm.MessageSend;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
extends ServiceImpl<UserLoginMapper, UserLogin> implements UserService
*/
@Service
public class SysLoginServiceImpl implements SysLoginService {

    //设置过期时间
    private static final long EXPIRE_DATE=180*30*60*100000;
    //token秘钥
    private static final String TOKEN_SECRET = "ZCEQIUBFKSJBFJH2020BQWE";

    @Autowired
    private AppRedisCacheManager appRedisCacheManager;
    @Autowired
    private SysUserMapper userMapper;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private RmStaffInfoMapper rmStaffInfoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public SysUserEntity queryByName(String username) {
        return userMapper.queryByName(username);
    }

    public Object test(String username){
        return username;
    }

    @Override
    public AppResponse loginIn(Map<String, Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        String direct="";
        LoginInfo loginInfo =null;
        String token="";
        String passWordSys="";
        String userId="";
        String userNbr="";
        String validInd="";
        try {
            String username= (String) map.get("username");
            String password= (String) map.get("password");//密码
            String verifyCode = (String) map.get("verifyCode");//验证码

            //验证用户是否被锁定
            String lock = stringRedisTemplate.opsForValue().get(username+"-lock");
            if(!GeneTool.isEmpty(lock)){
                long nowTime = System.currentTimeMillis() / 1000;
                long oldLong = Long.valueOf(lock);
                long sec=1800l-(nowTime-oldLong);
                long yu=sec/ 60 % 60;
                if(yu>0){
                    throw new RuntimeException("用户已锁定！请" + yu + "分钟后再来。");
                }
            }
            //用户名校验
            if(GeneTool.isEmpty(username)){
                throw new RuntimeException("用户名不能为空！");
            }

            //查询
            List<SysUserEntity>  sysUserList = userMapper.queryByTel(username);//监管人员
            List<Map<String,Object>> manager= rmStaffInfoMapper.getByTel(username);//教职人员
            int num=sysUserList.size()+manager.size();
            if(num>1){
                throw new RuntimeException("用户身份重复，请联系管理员！");
            }else if(num==0){
                throw new RuntimeException("该手机号未在系统添加使用，请添加后登录！");
            }else if(null!=sysUserList && sysUserList.size()>0){//01-监管人员
                SysUserEntity sysUser=sysUserList.get(0);
                passWordSys=sysUser.getPasswords();
                userId=String.valueOf(sysUser.getUserId());
                userNbr = sysUser.getUserNbr();
                validInd=sysUser.getValidInd();
                direct="01";
            }else if(null!=manager && manager.size()>0){//02-神职人员
                Map<String, Object> managerMap = manager.get(0);

                passWordSys= (String) managerMap.get("passwords");
                userId=username;
                userNbr= (String) managerMap.get("type");
                validInd=(String) managerMap.get("flag");
                direct="02";
            }

            if("0".equals(validInd) || "02".equals(validInd)){
                throw new RuntimeException("用户已被冻结，请联系管理员!");
            }
            Hash hash = this.transSalt(userId, password, userNbr);
            String pass=String.valueOf(hash);

            String ernum = stringRedisTemplate.opsForValue().get(username+"-pass");
            if(!passWordSys.equals(pass)){
                int errNum =1;
                if(!GeneTool.isEmpty(ernum)){
                    errNum = Integer.parseInt(ernum);
                    errNum++;
                }
                //登录时密码10分钟连续错误5次
                stringRedisTemplate.opsForValue().set(username+"-pass",String.valueOf(errNum),10*60, TimeUnit.SECONDS);
                //登录时密码连续错误5次锁定30分钟
                if(5==errNum){
                    long errTime = System.currentTimeMillis() / 1000 ;
                    stringRedisTemplate.opsForValue().set(username+"-lock",String.valueOf(errTime),30*60, TimeUnit.SECONDS);
                    stringRedisTemplate.delete(username+"-pass");
                    throw new RuntimeException("密码10分钟内连续错误5次，用户已锁定！请30分钟后再来！");
                }
                throw new RuntimeException("密码错误" + errNum + "次!");

            }
            //验证码验证
            /*AppResponse appResponse = this.checkVerifyCode(verifyCode, username);
            //验证码不正确
            if(ResultCode.FAILED.getCode()==appResponse.getCode()){
                throw new RuntimeException(appResponse.getMessage());
            }*/

            //通过UUID生成token字符串,并将其以string类型的数据保存在redis缓存中，key为token，value为username
            token= String.valueOf(UUID.randomUUID()).replaceAll("-","");
            stringRedisTemplate.opsForValue().set(token,username,180*24*60*60, TimeUnit.SECONDS);
            stringRedisTemplate.delete(username+"-pass");
            code=ResultCode.SUCCESS.getCode();
            message="登录成功！";

        } catch (IncorrectCredentialsException e) {
            code=ResultCode.FAILED.getCode();
            message="密码错误!";
        } catch (LockedAccountException e) {
            code=ResultCode.FAILED.getCode();
            message="登录失败，该用户已被冻结！";
        } catch (AuthenticationException e) {
            code=ResultCode.FAILED.getCode();
            message="该用户不存在！";
        } catch (RuntimeException e) {
            code=ResultCode.FAILED.getCode();
            message=e.getMessage();
        }catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="登陆失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,loginInfo,direct,token);
    }

    @Override
    public AppResponse checkVerifyCode(String verifyCodes, String userName) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        try {
            //验证验证码
            Map<String,Object> map= (Map<String, Object>) appRedisCacheManager.get("verifyCode" + userName);
            if(null==map || map.size()<=0){
                message="验证码已过期，请重新发送！";
                throw new RuntimeException(message);
            }
            Object username = map.get("username");
            Object createTime = map.get("createTime");
            Object yanZhenMa = map.get("yanZhenMa");
            if(!userName.equals(username)){
                message="手机号不正确，请使用验证码发送的手机号登录！";
                throw new RuntimeException(message);
            }
            System.out.println("memPhone:"+userName+",传入的验证码是："+verifyCodes);
            if(null==verifyCodes || "".equals(verifyCodes)){
                message="验证码不能为空，请填写验证码！";
                throw new RuntimeException(message);
            }else if(null==yanZhenMa || "".equals(yanZhenMa)){
                message="验证码已过期，请重新发送！";
                throw new RuntimeException(message);
            }else if(!yanZhenMa.equals(verifyCodes)){
                message="验证码不正确，请重新填写！";
                throw new RuntimeException(message);
            }else if(yanZhenMa.equals(verifyCodes)){
                System.out.println("验证码正确");
            }
            code=ResultCode.SUCCESS.getCode();
            message="验证码验证成功！";
        }catch (RuntimeException re) {
            if(message.isEmpty()){
                message="验证码处理失败";
            }
            re.printStackTrace();
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="验证码处理失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    public AppResponse sendVerifyCode(String username) {
        long code= ResultCode.FAILED.getCode();
        String message="验证码发送失败";
        String verifyCode="";
        try {
            if(GeneTool.isEmpty(username)){
                throw new RuntimeException("手机号不能为空！");
            }
            //验证号码在系统是否存在
            List<SysUserEntity>  sysUserList = userMapper.queryByTel(username);//监管人员
            List<Map<String,Object>> manager= rmStaffInfoMapper.getByTel(username);//教职人员
            int num=sysUserList.size()+manager.size();
            if(num>1){
                throw new RuntimeException("用户身份重复，请联系管理员！");
            }else if(num==0){
                throw new RuntimeException("手机号（"+username+"）在系统不存在，请在系统添加后使用！");
            }else{
                String validInd ="0";
                if(sysUserList.size()>0){
                    validInd = sysUserList.get(0).getValidInd();
                }else{
                    validInd = (String) manager.get(0).get("");
                }
                if("0".equals(validInd)){
                    throw new RuntimeException("号码("+username+")在系统内已失效!");
                }
            }

            //随机生成验证码存入redis
            verifyCode = this.saveCodeRedis(username);
            code=ResultCode.SUCCESS.getCode();
            message="验证码发送成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="验证码发送失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,verifyCode);
    }

    @Override
    public AppResponse updatePassword(String verifyCode, String password, String username) {
        long code = ResultCode.FAILED.getCode();
        String message = "";
        try {

            //查询
            //String identity="";
            int id=0;
            List<SysUserEntity> sysUserList = userMapper.queryByTel(username);//监管人员
            List<Map<String,Object>> manager= rmStaffInfoMapper.getByTel(username);//神职人员
            String userId="";
            String userNbr="";
            int num=sysUserList.size()+manager.size();
            if(num>1){
                message="用户手机号重复，请联系管理员！";
                throw new RuntimeException(message);
            }else if(num==0){
                message="该手机号未在系统添加使用，请添加后登录！";
                throw new RuntimeException(message);
            }else if(null!=sysUserList && sysUserList.size()>0){
                SysUserEntity sysUser =sysUserList.get(0);
                //identity=sysUser.getIdentity();
                id=sysUser.getUserId();
                userId=String.valueOf(sysUser.getUserId());
                userNbr = sysUser.getUserNbr();
            }else if(null!=manager && manager.size()>0){
                Map<String, Object> managerMap = manager.get(0);
                //identity= (String) managerMap.get("type");
                id= (int) managerMap.get("id");
                userId=username;
                userNbr= (String) managerMap.get("type");
            }
            //验证码验证
            AppResponse appResponse = this.checkVerifyCode(verifyCode, username);
            //验证码不正确
            if(ResultCode.FAILED.getCode()==appResponse.getCode()){
                return appResponse;
            }
            //salt加密
            Hash hash = this.transSalt(userId,password,userNbr);
            String pass=String.valueOf(hash);
            //修改密码
            if((null!=sysUserList && sysUserList.size()>0)){//修改监管人员
                userMapper.updatePassword(pass,id,TimeTool.getYmdHms());
            }else if(null!=manager && manager.size()>0){//修改神职人员
                rmStaffInfoMapper.updatePassword(pass,password,id,username,TimeTool.getYmdHms());
            }

            code=ResultCode.SUCCESS.getCode();
            message="密码修改成功！";
        }catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            message="密码修改失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message);
    }

    @Override
    public AppResponse loginInPC(Map<String, Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        String token="";

        try {
            String username= (String) map.get("username");//用户
            String password= (String) map.get("password");//密码
            String verifyCode = (String) map.get("verifyCode");//验证码
            //用户名校验
            if(GeneTool.isEmpty(username)){
                throw new RuntimeException("用户名不能为空！");
            }
            //查询
            List<SysUserEntity>  sysUserList = userMapper.queryByTel(username);//监管人员
            if(sysUserList.size()>1){
                throw new RuntimeException("用户重复，请联系管理员！");
            }else if(null==sysUserList || sysUserList.size()==0){
                throw new RuntimeException("手机号("+username+")未在系统添加使用，请添加后登录！");
            }
            SysUserEntity user = sysUserList.get(0);
            int usId = user.getUserId();
            String userNbr = user.getUserNbr();
            //验证用户是否被锁定
            String lock = stringRedisTemplate.opsForValue().get(usId+"-lock");
            if(!GeneTool.isEmpty(lock)){
                long nowTime = System.currentTimeMillis() / 1000;
                long oldLong = Long.valueOf(lock);
                long sec=1800l-(nowTime-oldLong);
                long yu=sec/ 60 % 60;
                if(yu>0){
                    throw new RuntimeException("用户已锁定！请" + yu + "分钟后再来。");
                }
            }

            Hash hash = this.transSalt(String.valueOf(usId), password, userNbr);
            String pass=String.valueOf(hash);
            String passWordSys = user.getPasswords();

            String ernum = stringRedisTemplate.opsForValue().get(usId+"-pass");
            if(!passWordSys.equals(pass)){
                int errNum =1;
                if(!GeneTool.isEmpty(ernum)){
                    errNum = Integer.parseInt(ernum);
                    errNum++;
                }
                //登录时密码10分钟连续错误5次
                stringRedisTemplate.opsForValue().set(usId+"-pass",String.valueOf(errNum),10*60, TimeUnit.SECONDS);
                //登录时密码连续错误5次锁定30分钟
                if(3==errNum){
                    long errTime = System.currentTimeMillis() / 1000 ;
                    stringRedisTemplate.opsForValue().set(usId+"-lock",String.valueOf(errTime),30*60, TimeUnit.SECONDS);
                    stringRedisTemplate.delete(usId+"-pass");
                    throw new RuntimeException("密码10分钟内连续错误5次，用户已锁定！请30分钟后再来！");
                }
                throw new RuntimeException("密码错误" + errNum + "次!");

            }
            //验证码验证
            /*AppResponse appResponse = this.checkVerifyCode(verifyCode, username);
            //验证码不正确
            if(ResultCode.FAILED.getCode()==appResponse.getCode()){
                throw new RuntimeException(appResponse.getMessage());
            }*/

            //通过UUID生成token字符串,并将其以string类型的数据保存在redis缓存中，key为token，value为username
            token= String.valueOf(UUID.randomUUID()).replaceAll("-","");
            stringRedisTemplate.opsForValue().set(token,username,2*60*60, TimeUnit.SECONDS);
            stringRedisTemplate.delete(usId+"-pass");
            code=ResultCode.SUCCESS.getCode();
            message="登录成功！";

        } catch (IncorrectCredentialsException e) {
            code=ResultCode.FAILED.getCode();
            message="密码错误!";
        } catch (LockedAccountException e) {
            code=ResultCode.FAILED.getCode();
            message="登录失败，该用户已被冻结！";
        } catch (AuthenticationException e) {
            code=ResultCode.FAILED.getCode();
            message="该用户不存在！";
        } catch (RuntimeException e) {
            code=ResultCode.FAILED.getCode();
            message=e.getMessage();
        }catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="登陆失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,token);
    }

    /**
     * 随机生成验证码存入redis
     * @param username
     */
    public String saveCodeRedis (String username) throws Exception {
        //随机生成验证码存入redis
        String verifyCode = String.valueOf(new Random().nextInt(999999));
        String contents="【智云科技】"+verifyCode+"(登录验证码，5分钟内有效)。请勿向任何人泄露，以免造成任何损失。";
        List<Map<String,Object>> messageList=new ArrayList<>();
        Map<String,Object> smap=new HashMap<>();
        smap.put("phone",username);
        smap.put("content",contents);
        messageList.add(smap);
        //发送
        //SendVerifyCode.sendVerifyCode(contents, username);
        MessageSend.sendSmsOne(messageList);
        //封装参数
        JSONObject json = new JSONObject();
        json.put("username",username);
        json.put("yanZhenMa",verifyCode);
        json.put("createTime",System.currentTimeMillis());
        // 将认证码存入redis，有效时长5分钟
        //redisUtils.set("verifyCode"+memPhone,json,5L, TimeUnit.MINUTES);
        String key="verifyCode"+username;
        //redis.set(key,json);
        appRedisCacheManager.setUnion(key,json,5L,TimeUnit.MINUTES);
        return verifyCode;
    }

    /**
     * token
     * @param userinfo
     * @return
     */
    public static String token (SysUserEntity userinfo){
        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis()+EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String,Object> header = new HashMap<>();
            header.put("typ","JWT");
            header.put("alg","HS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("username",userinfo.getLoginNm())
                    //.withClaim("password",userinfo.getPasswords())
                    .withClaim("userNbr",userinfo.getUserNbr())
                    .withClaim("identity",userinfo.getIdentity())
                    .withClaim("ofcId", userinfo.getOfcId())
                    .withClaim("userId",userinfo.getUserId())
                    .withClaim("expireAt",date)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
        return token;
    }

    /**
     * 登录
     * @param username
     * @return
     */
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

    /**
     * 密码加盐
     * @param name
     * @param salts
     * @param password
     * @return
     */
    public Hash transSalt(String name,String password, String salts){
        String hashAlgorithmName = "MD5";//加密方式
        char[]  crdentials=(char[])(password != null ? password.toCharArray() : null);
        Object salt=ByteSource.Util.bytes(name+salts);
        int hashIterations = 1024;//加密1024次
        Hash result = new SimpleHash(hashAlgorithmName,crdentials,salt,hashIterations);
        return result;
    }
}
