/*
package com.religion.zhiyun.user.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ShiroController {
    @Resource
    private LoginService loginService;
    */
/**
     * 登录方法
     * @param map
     * @return
     *//*

    @RequestMapping(value = "/api/ajaxLogin", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result ajaxLogin(@RequestBody Map map) {
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        //String password=DigestUtils.md5DigestAsHex(map.get("password").toString().getBytes());
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            subject.login(token);
            LoginInfo loginInfo = loginService.getLoginInfo(username);
            return ResultFactory.buildSuccessResult(loginInfo);// 将用户的角色和权限发送到前台
        } catch (IncorrectCredentialsException e) {
            return ResultFactory.buildFailResult("密码错误");
        } catch (LockedAccountException e) {
            return ResultFactory.buildFailResult("登录失败，该用户已被冻结");
        } catch (AuthenticationException e) {
            return ResultFactory.buildFailResult("该用户不存在");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResultFactory.buildFailResult("登陆失败");
    }

    */
/**
     * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
     * @return
     *//*

    @RequestMapping(value = "/unauth")
    @ResponseBody
    public Object unauth() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", "1000000");
        map.put("msg", "未登录");
        return map;
    }
}

*/
