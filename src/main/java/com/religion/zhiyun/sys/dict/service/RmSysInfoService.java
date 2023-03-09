package com.religion.zhiyun.sys.dict.service;

import com.religion.zhiyun.sys.dict.entity.SysEntity;
import com.religion.zhiyun.utils.response.PcResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.util.List;

public interface RmSysInfoService {

    RespPageBean getSysDict(String dictTypeCd);
    void addSys(SysEntity sysEntity);
    void updateSys(SysEntity sysEntity);
    void deleteSys(String sysId);
}
