package com.religion.zhiyun.user.service;

import com.religion.zhiyun.user.entity.SysUserEntity;

public interface LoginService {
    SysUserEntity queryByName(String username);
    Object test(String username);
}
