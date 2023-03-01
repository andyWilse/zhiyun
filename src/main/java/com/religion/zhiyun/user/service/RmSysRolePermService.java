package com.religion.zhiyun.user.service;

import com.religion.zhiyun.user.entity.SysPermission;

import java.util.List;

public interface RmSysRolePermService {
    /**
     * 根据角色获取权限
     * @param roleId
     * @return
     */
    List<SysPermission> getRolePerm(String roleId);
}
