package com.religion.zhiyun.user.dao;


import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.entity.UserVenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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

    /**
     * 删除场所用户
     * @param uvId
     * @return
     */
    int deleteSrUser(@Param("uvId") String uvId,@Param("lastModifyTime")  Timestamp lastModifyTime);
}
