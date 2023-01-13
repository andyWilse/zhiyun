package com.religion.zhiyun.login.controller;

import com.religion.zhiyun.login.api.CommonResult;
import com.religion.zhiyun.login.entity.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Eli Shaw
 * @Date: 2019-11-14 11:33:26
 * @Description：
 */
@RestController
public class LoginController {

    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    public CommonResult login(@RequestBody User user) {
        if (user.getUsername().equals("admin") && user.getPassword().equals("123456"))
            return CommonResult.success("admin");
        else
            return CommonResult.validateFailed();
    }
}
