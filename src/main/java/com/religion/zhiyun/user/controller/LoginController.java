package com.religion.zhiyun.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.login.service.SysLoginService;
import com.religion.zhiyun.login.service.impl.SysLoginServiceImpl;
import com.religion.zhiyun.utils.Tool.HttpClient;
import com.religion.zhiyun.utils.response.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private SysLoginService loginService;

    @PostMapping("/in")
    public Map<String,Object> login(@RequestBody Map map) {
        Map<String,Object> result = new HashMap<>();
        SysUserEntity userinfo = loginService.queryByName(map.get("username").toString());
        if(userinfo!=null){
            if(userinfo.getPasswords().equals(DigestUtils.md5DigestAsHex(map.get("password").toString().getBytes()))){
                result.put("userNbr",userinfo.getUserNbr());
                result.put("token", SysLoginServiceImpl.token(userinfo));
                result.put("msg","success");
                result.put("code", ResultCode.SUCCESS.code());
            }else {
                result.put("result","账号或密码错误");
                result.put("msg","error");
                result.put("code", ResultCode.USER_LOGIN_ERROR.code());
            }
        }else {
            result.put("result","账号或密码错误");
            result.put("msg","error");
            result.put("code", ResultCode.USER_LOGIN_ERROR.code());

        }
        return result;
    }
    @RequestMapping("/test")
    public Object test(@RequestBody Map map) {
        return JSONObject.parseObject(HttpClient.sendGet("https://api.seniverse.com/v3/weather/now.json?key=Sjy1enyaTtzn80Etd&location=xian&language=zh-Hans&unit=c"));

    }

}
