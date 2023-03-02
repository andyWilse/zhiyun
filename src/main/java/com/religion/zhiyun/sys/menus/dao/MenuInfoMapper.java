package com.religion.zhiyun.sys.menus.dao;

import com.religion.zhiyun.sys.menus.entity.MenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface MenuInfoMapper {
    //查询
    List<MenuEntity> findRoot();

    //查询父菜单
    List<MenuEntity> findMenus(@Param("resourceType") String resourceType);

    //查询子菜单
    List<MenuEntity> findAllChilds(@Param("menuPrtIds") String[] menuPrtIds);

    /**
     * 获取一级菜单
     * @return
     */
    List<Map<String,Object>> findOneTree();

    /**
     * 获取二级菜单
     * @return
     */
    List<Map<String,Object>> findTwoTree(@Param("parentId") Integer parentId);

    /**
     * 获取按钮
     * @return
     */
    List<Map<String,Object>> findButtonTree(@Param("parentId") Integer parentId);
}
