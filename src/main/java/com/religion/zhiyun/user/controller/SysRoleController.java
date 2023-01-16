package com.religion.zhiyun.user.controller;

import com.religion.zhiyun.sys.dict.entity.SysEntity;
import com.religion.zhiyun.user.entity.RoleEntity;
import com.religion.zhiyun.user.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
