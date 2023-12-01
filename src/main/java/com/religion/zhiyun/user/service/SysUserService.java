package com.religion.zhiyun.user.service;

import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
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
    PageResponse getUsersByPage(Map<String, Object> map,String token);

    /**
     * 新增
     * @param sysUserEntity
     */
    RespPageBean add(SysUserEntity sysUserEntity,String token);

    /**
     * 修改
     * @param sysUserEntity
     */
    PageResponse update(SysUserEntity sysUserEntity,String token);

    /**
     * 密码修改
     * @param map
     */
    PageResponse updatePassword(Map<String,String> map,String token);

    /**
     * 密码修改(用户管理)
     * @param map
     */
    PageResponse modifyPassword(Map<String,Object> map,String token);

    /**
     * 删除
     * @param userId
     */
    void delete(int userId,String token);

    //根据用户编号查询数据
    SysUserEntity queryByNbr(@Param("userNbr") String userNbr);

    //根据用户名
    SysUserEntity queryByName(@Param("userNm") String userNm);

    //根据电话查询
    SysUserEntity queryByTel(@Param("userMobile") String userMobile);

    /**
     * 获取登录用户信息
     * @param token
     */
    PageResponse getUserInfo(String token);
    /**
     * 获取修改用户信息
     * @param userId
     */
    PageResponse getModifyUser(String userId);


}
