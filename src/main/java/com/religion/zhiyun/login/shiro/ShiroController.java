package com.religion.zhiyun.login.shiro;

import com.religion.zhiyun.login.entity.LoginInfo;
import com.religion.zhiyun.login.entity.Result;
import com.religion.zhiyun.login.entity.ResultFactory;
import com.religion.zhiyun.login.service.LoginService;
import com.religion.zhiyun.utils.redis.AppRedisCacheManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@CrossOrigin
public class ShiroController {
    @Resource
    private LoginService loginService;

    @Autowired
    private AppRedisCacheManager manage;
/**
     * 登录方法
     * @param map
     * @return*/
    @RequestMapping(value = "/api/ajaxLogin", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result ajaxLogin(@RequestBody Map map, HttpServletResponse response) {
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        //String password=DigestUtils.md5DigestAsHex(map.get("password").toString().getBytes());
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);
        try {
            subject.login(token);
            LoginInfo loginInfo = loginService.getLoginInfo(username);

            HttpServletRequest requests = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String userNm=username+  UUID.randomUUID();
            requests.getSession().setAttribute(userNm, subject);
            //manage.setUnion(userNm,subject,5L, TimeUnit.MINUTES);

            Cookie cookie = new Cookie(userNm,userNm);

            cookie.setMaxAge(30);
            response.addCookie(cookie);
            response.addCookie(cookie);
            return ResultFactory.buildSuccessResult(loginInfo,userNm);// 将用户的角色和权限发送到前台

        } catch (IncorrectCredentialsException e) {
            return ResultFactory.buildFailResult("密码错误");
        } catch (LockedAccountException e) {
            return ResultFactory.buildFailResult("登录失败，该用户已被冻结");
        } catch (AuthenticationException e) {
            return ResultFactory.buildFailResult("该用户不存在");
        }catch (RuntimeException e) {
            return ResultFactory.buildFailResult(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResultFactory.buildFailResult("登陆失败");
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

    @RequestMapping(value = "/logout")
    @ResponseBody
    public Object logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();   //用户退出
        subject.getSession().setTimeout(0);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("msg", "退出登录");
        return map;
    }

}

