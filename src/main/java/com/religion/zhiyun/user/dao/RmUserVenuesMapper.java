package com.religion.zhiyun.user.dao;


import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.entity.UserVenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RmUserVenuesMapper {

    /**
     * 获取
     * @return
     */
    List<SysUserEntity> getUsers();
    /**
     * 新增
     * @param uvEntity
     */
    int add(UserVenuesEntity uvEntity);
}
