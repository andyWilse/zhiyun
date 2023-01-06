package com.religion.zhiyun.sys.service;

import com.religion.zhiyun.sys.entity.SysEntity;

import java.util.List;

public interface RmSysInfoService {

    List<SysEntity> allSys();
    void addSys(SysEntity sysEntity);
    void updateSys(SysEntity sysEntity);
    void deleteSys(String sysId);
}
