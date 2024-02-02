package com.religion.zhiyun.user.controller;

import com.religion.zhiyun.login.http.inter.DecryptRequest;
import com.religion.zhiyun.login.http.inter.EncryptResponse;
import com.religion.zhiyun.user.entity.RoleEntity;
import com.religion.zhiyun.user.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@DecryptRequest(true)
@EncryptResponse(true)
@RestController
@RequestMapping("/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/getRoles")
    @ResponseBody
    public Object[] queryRoles(){
        List<RoleEntity> list = sysRoleService.queryRoles();
        return list.toArray();
    }
}
