package com.religion.zhiyun.user.service.impl;

import com.religion.zhiyun.user.dao.RmSysRolePermMapper;
import com.religion.zhiyun.user.entity.SysPermission;
import com.religion.zhiyun.user.service.RmSysRolePermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RmSysRolePermServiceImpl implements RmSysRolePermService {
    @Autowired
    RmSysRolePermMapper rmSysRolePermMapper;
    @Override
    public List<SysPermission> getRolePerm(String roleId) {
        return rmSysRolePermMapper.getRolePerm(roleId);
    }
}
