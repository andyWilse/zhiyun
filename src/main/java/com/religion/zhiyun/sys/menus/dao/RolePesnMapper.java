package com.religion.zhiyun.sys.menus.dao;

import com.religion.zhiyun.sys.menus.entity.MenuEntity;
import com.religion.zhiyun.sys.menus.entity.PesnEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RolePesnMapper {
    //删除
    long delete(@Param("roleId")String roleId);

    //保存
    long add(@Param("pmsnCd") String pmsnCd, @Param("roleId")String roleId);

    //获取角色下所有菜单
    List<String> getMenuByRole(@Param("roleId") String roleId);

}
