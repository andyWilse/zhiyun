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
     * @param vo
     * @return
     */
    int deleteSrMap(@Param("vo") UserVenuesEntity vo);

    /**
     * 获取用户
     * @param uvUserId
     * @param uvVenuesId
     * @return
     */
    List<UserVenuesEntity> getUserVenues(@Param("uvUserId") int uvUserId,@Param("uvVenuesId") int uvVenuesId);
}
