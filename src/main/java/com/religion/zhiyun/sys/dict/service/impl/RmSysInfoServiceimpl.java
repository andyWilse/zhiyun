package com.religion.zhiyun.sys.dict.service.impl;

import com.religion.zhiyun.sys.dict.dao.RmSysInfoMapper;
import com.religion.zhiyun.sys.dict.entity.SysEntity;
import com.religion.zhiyun.sys.dict.service.RmSysInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RmSysInfoServiceimpl implements RmSysInfoService {
    @Autowired
    private RmSysInfoMapper rmSysInfoMapper;


    @Override
    public List<SysEntity> getSysDicts(String dictTypeCd) {
        return rmSysInfoMapper.getSysDicts(dictTypeCd);
    }

    @Override
    public void addSys(SysEntity sysEntity) {
        rmSysInfoMapper.addSys(sysEntity);
    }

    @Override
    public void updateSys(SysEntity sysEntity) {
        rmSysInfoMapper.updateSys(sysEntity);
    }

    @Override
    public void deleteSys(String sysId) {
        rmSysInfoMapper.deleteSys(sysId);
    }
}
