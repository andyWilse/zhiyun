package com.religion.zhiyun.user.dao;

import com.religion.zhiyun.user.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface SysRoleMapper {
    /**
     * 获取所有有效角色
     * @return
     */
    List<RoleEntity> queryRoles();

    /**
     * 获取所有有效角色
     * @return
     */
    List<RoleEntity> selectRoleByUserId(@Param("userId") int userId);

    /**
     * 获取角色
     * @param roleId
     * @return
     */
    String getRoleNm(String roleId);

}
