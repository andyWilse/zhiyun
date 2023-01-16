package com.religion.zhiyun.sys.dict.service;

import com.religion.zhiyun.sys.dict.entity.SysEntity;

import java.util.List;

public interface RmSysInfoService {

    List<SysEntity> getSysDicts(String dictTypeCd);
    void addSys(SysEntity sysEntity);
    void updateSys(SysEntity sysEntity);
    void deleteSys(String sysId);
}
