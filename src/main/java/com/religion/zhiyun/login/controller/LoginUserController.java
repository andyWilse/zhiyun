package com.religion.zhiyun.login.controller;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.login.entity.LoginResp;
import com.religion.zhiyun.login.service.SysLoginService;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.response.AppResponse;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: wang
 * @Date: 2019-11-14 11:33:26
 * @Description：
 */
@RestController
public class LoginUserController {

    @Autowired
    private SysUserService sysUserService;

    //通过java去操作redis缓存string类型的数据
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SysLoginService loginService;

/**
 * 登录方法
 * @param map
 * @return*/

    @PostMapping("/login")
    public LoginResp ajaxLogin(@RequestBody Map map) {
        try {
            String username = (String) map.get("username");
            String password = (String) map.get("password");
            String verifyCode = (String) map.get("verifyCode");

            SysUserEntity user = sysUserService.queryByTel(username);
            if(user==null){
                throw new RuntimeException("用户不存在!");
            }else if("0".equals(user.getValidInd())){
                throw new RuntimeException("登录失败，该用户已失效!");
            }
            int userId = user.getUserId();
            //验证用户是否被锁定
            String lock = stringRedisTemplate.opsForValue().get(String.valueOf(userId)+"-lock");
            if(!GeneTool.isEmpty(lock)){
                long nowTime = System.currentTimeMillis() / 1000 ;
                long oldLong = Long.valueOf(nowTime);
                long sec=1800l-(nowTime-oldLong);
                long yu=sec;
                throw new RuntimeException("10分钟内密码错误大于等于5次，账户锁定。请" + yu + "分钟后再来");
            }
            String userNbr = user.getUserNbr();
            Hash hash = this.transSalt(String.valueOf(userId), password, userNbr);
            String pass=String.valueOf(hash);
            String passwords = user.getPasswords();

            String num = stringRedisTemplate.opsForValue().get(String.valueOf(userId));
            if(!passwords.equals(pass)){
                int errNum =1;
                if(!GeneTool.isEmpty(num)){
                    errNum = Integer.parseInt(num);
                    errNum++;
                }
                //登录时密码10分钟连续错误5次
                stringRedisTemplate.opsForValue().set(String.valueOf(userId),String.valueOf(errNum),10*60, TimeUnit.SECONDS);
                //登录时密码连续错误5次锁定30分钟
                if(5==errNum){
                    long errSec = System.currentTimeMillis() / 1000;
                    stringRedisTemplate.opsForValue().set(String.valueOf(userId)+"-lock",String.valueOf(errSec),30*60, TimeUnit.SECONDS);
                }
                throw new RuntimeException("密码错误" + errNum + "次!");
            }
            //验证码验证
            AppResponse appResponse = loginService.checkVerifyCode(verifyCode, username);
            //验证码不正确
            if(ResultCode.FAILED.getCode()==appResponse.getCode()){
                throw new RuntimeException("验证码错误!");
            }

            //通过UUID生成token字符串,并将其以string类型的数据保存在redis缓存中，key为token，value为username
            String token= String.valueOf(UUID.randomUUID()).replaceAll("-","");
            stringRedisTemplate.opsForValue().set(token,username,180*24*60*60, TimeUnit.SECONDS);
            //stringRedisTemplate.opsForValue().set(String.valueOf(usId),"0");

            return LoginResp.success("登录成功").add("token",token); // 将用户的角色和权限发送到前台
        } catch (IncorrectCredentialsException e) {
            return LoginResp.fail("密码错误");
        } catch (LockedAccountException e) {
            return LoginResp.fail("登录失败，该用户已被冻结");
        } catch (AuthenticationException e) {
            return LoginResp.fail("该用户不存在");
        }catch (RuntimeException e) {
            return LoginResp.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return LoginResp.fail("登陆失败");
    }


/**
 * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
 * @return*/

    @RequestMapping(value = "/unauth")
    @ResponseBody
    public Object unauth() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", "1000000");
        map.put("msg", "未登录");
        return map;
    }

    //注销接口
    @PostMapping("/logout")
    public LoginResp logOut(@RequestHeader("token")String token){
        //删除redis缓存中的token
        stringRedisTemplate.delete(token);
        return LoginResp.success("注销成功");
    }

    //默认
    @PostMapping("/loginIndex")
    public LoginResp loginIndex(){
        return LoginResp.success("默认登录入口！");
    }

    /**
     * 密码加盐
     * @param name
     * @param salts
     * @param password
     * @return
     */
    public Hash transSalt(String name,String password, String salts){
        ByteSource admin = ByteSource.Util.bytes(name+salts);
        String hashAlgorithmName = "MD5";//加密方式
        char[]  crdentials=(char[])(password != null ? password.toCharArray() : null);
        Object salt=ByteSource.Util.bytes(name+salts);
        int hashIterations = 1024;//加密1024次
        Hash result = new SimpleHash(hashAlgorithmName,crdentials,salt,hashIterations);
        return result;
    }
}
