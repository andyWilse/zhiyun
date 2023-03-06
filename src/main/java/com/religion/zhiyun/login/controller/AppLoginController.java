package com.religion.zhiyun.login.controller;

import com.religion.zhiyun.login.service.SysLoginService;
import com.religion.zhiyun.utils.response.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/app")
public class AppLoginController {

    @Autowired
    private SysLoginService LoginService;

    /**
     * 发送验证码(手机号)
     * @param username
     * @return
     */
    @RequestMapping(value = "/sendVerifyCode")
    @ResponseBody
    public AppResponse sendVerifyCode(@RequestParam String username) {
        return LoginService.sendVerifyCode(username);
    }

    @PostMapping("/loginIn")
    public AppResponse login(@RequestParam Map<String, Object> map) {
        String username= (String) map.get("username");
        String password= (String) map.get("password");//密码
        return LoginService.loginIn(username,password);
    }

    @PostMapping("/updatePassword")
    public AppResponse updatePassword(@RequestParam Map<String, Object> map) {
        String username = (String) map.get("username");//电话
        String verifyCode = (String) map.get("verifyCode");//验证码
        String password= (String) map.get("newPassword");//密码
        return LoginService.updatePassword(verifyCode,password,username);
    }


}
