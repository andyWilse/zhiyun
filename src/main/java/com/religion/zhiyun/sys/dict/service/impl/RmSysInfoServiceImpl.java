package com.religion.zhiyun.sys.dict.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.dict.dao.RmSysInfoMapper;
import com.religion.zhiyun.sys.dict.entity.SysEntity;
import com.religion.zhiyun.sys.dict.service.RmSysInfoService;
import com.religion.zhiyun.utils.response.PcResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RmSysInfoServiceImpl implements RmSysInfoService {
    @Autowired
    private RmSysInfoMapper rmSysInfoMapper;

    @Override
    public RespPageBean getSysDict(String dictTypeCd) {
        long code= ResultCode.FAILED.getCode();
        String message="字典信息获取！";
        List<SysEntity> list=new ArrayList<>();
        try {
            list= rmSysInfoMapper.getSysDicts(dictTypeCd);
            code= ResultCode.SUCCESS.getCode();
            message="字典信息获取成功！";
        } catch (Exception e) {
            message="字典信息获取失败！";
            e.printStackTrace();
        }

        return new RespPageBean(code,message,list.toArray());
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
