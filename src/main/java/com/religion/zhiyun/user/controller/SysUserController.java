package com.religion.zhiyun.user.controller;

import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/find")
    public PageResponse getUsersByPage(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        return sysUserService.getUsersByPage(map,token);
    }

    @PostMapping("/add")
    @ResponseBody
    public RespPageBean add(@RequestBody SysUserEntity sysUserEntity) {
        return sysUserService.add(sysUserEntity);
    }

    @PostMapping("/update")
    public PageResponse update(@RequestBody SysUserEntity sysUserEntity) {
        return sysUserService.update(sysUserEntity);
    }

    @PostMapping("/delete/{userId}")
    public void delete(@PathVariable int userId) {
        sysUserService.delete(userId);
    }

    @PostMapping("/update/password")
    public RespPageBean updatePassword(@RequestBody Map<String,String> map) {
        return sysUserService.updatePassword(map);
    }

    @GetMapping("/getUser")
    public PageResponse getUserInfo(@RequestHeader("token")String token){
        return sysUserService.getUserInfo(token);
    }

    @GetMapping("/getMoUser")
    public PageResponse getModifyUser(@RequestParam String userId){
        return sysUserService.getModifyUser(userId);
    }

}
