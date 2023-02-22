package com.religion.zhiyun.user.service;

import com.religion.zhiyun.user.entity.RoleEntity;

import java.util.List;

public interface SysRoleService {
    /**
     * 获取所有有效角色
     * @return
     */
    List<RoleEntity> queryRoles();

    List<RoleEntity> selectRoleByUserId(int userId);
}
