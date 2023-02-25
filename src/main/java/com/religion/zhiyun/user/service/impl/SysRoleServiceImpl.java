package com.religion.zhiyun.user.service.impl;

import com.religion.zhiyun.user.dao.SysRoleMapper;
import com.religion.zhiyun.user.entity.RoleEntity;
import com.religion.zhiyun.user.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Override
    public List<RoleEntity> queryRoles() {
        return sysRoleMapper.queryRoles();
    }

    @Override
    public List<RoleEntity> selectRoleByUserId(int userId) {
        return sysRoleMapper.selectRoleByUserId(userId);
    }
}
