package com.religion.zhiyun.user.service;

import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.apache.ibatis.annotations.Param;

import java.io.IOException;
import java.util.Map;

public interface SysUserService {
    /**
     * 用户分页查询
     * @return
     * @throws IOException
     */
    RespPageBean getUsersByPage(Map<String, Object> map);

    /**
     * 新增
     * @param sysUserEntity
     */
    RespPageBean add(SysUserEntity sysUserEntity);

    /**
     * 修改
     * @param sysUserEntity
     */
    RespPageBean update(SysUserEntity sysUserEntity);

    /**
     * 密码修改
     * @param map
     */
    RespPageBean updatePassword(Map<String,String> map);

    /**
     * 删除
     * @param userId
     */
    void delete(int userId);

    //根据用户编号查询数据
    SysUserEntity queryByNbr(@Param("userNbr") String userNbr);

    //根据用户名
    SysUserEntity queryByName(@Param("userNm") String userNm);

    //根据电话查询
    SysUserEntity queryByTel(@Param("userMobile") String userMobile);


}
