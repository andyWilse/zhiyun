package com.religion.zhiyun.login.service;

import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.AppResponse;

public interface SysLoginService {
    SysUserEntity queryByName(String username);
    Object test(String username);

    /**
     *  app登录
     * @param username
     * @param password
     * @return
     */
    AppResponse loginIn(String username,String password);

    /**
     * 验证
     * @param verifyCode
     * @param userName
     * @return
     */
    AppResponse checkVerifyCode(String verifyCode,String userName);

    /**
     * 发送验证码
     * @param username
     * @return
     */
    AppResponse sendVerifyCode(String username);

    /**
     *  更新密码
     * @param username
     * @param verifyCode
     * @param password
     * @return
     */
    AppResponse updatePassword(String verifyCode,String password,String username);

}
