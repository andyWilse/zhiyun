package com.religion.zhiyun.staff.service.impl;

import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RmStaffInfoServiceimpl implements RmStaffInfoService {
    @Autowired
    private RmStaffInfoMapper staffInfoMapper;

    @Override
    public void add(StaffEntity staffEntity) {
        staffInfoMapper.add(staffEntity);
    }
}
