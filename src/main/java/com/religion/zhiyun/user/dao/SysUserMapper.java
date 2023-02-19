package com.religion.zhiyun.user.dao;

import com.religion.zhiyun.user.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Mapper
@Repository
public interface SysUserMapper {
    //查询
    public List<SysUserEntity> queryAll();

    //根据用户名查询数据
    SysUserEntity queryByName(@Param("loginNm") String loginNm);

    //根据用户编号查询数据
    SysUserEntity queryByNbr(@Param("userNbr") String userNbr);

    //根据用户id查询数据
    SysUserEntity queryByUserId(@Param("userId") String userId);

    /**
     * 分页拆查询
     * @param page
     * @param size
     * @param identity
     * @param loginNm
     * @return
     * @throws IOException
     */
    List<SysUserEntity>  getUsersByPage(Integer page, Integer size, String identity, String loginNm) throws IOException;

    /**
     * 获取客户编号
     * @return
     */
    Long getMaxUserNbr();

    /**总条数**/
    Long getTotal();

    /**
     * 新增
     * @param sysUserEntity
     */
    int add(SysUserEntity sysUserEntity);

    /**
     * 修改
     * @param sysUserEntity
     */
    void update(SysUserEntity sysUserEntity);

    /**
     * 删除
     * @param userId
     */
    void delete(int userId);

    /**
     * 查询发起人
     * @param procInstId
     * @return
     */
    String queryStarter(@Param("procInstId") String procInstId);

}
