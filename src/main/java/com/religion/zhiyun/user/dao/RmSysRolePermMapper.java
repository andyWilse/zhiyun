package com.religion.zhiyun.user.dao;

import com.religion.zhiyun.user.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RmSysRolePermMapper {
    /**
     * 根据角色获取权限
     * @param roleId
     * @return
     */
    List<SysPermission> getRolePerm(String roleId);
}
