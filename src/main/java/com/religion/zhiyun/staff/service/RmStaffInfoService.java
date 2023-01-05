package com.religion.zhiyun.staff.service;

import com.religion.zhiyun.staff.entity.StaffEntity;

import java.util.List;

public interface RmStaffInfoService {

    void add(StaffEntity staffEntity);

    List<StaffEntity> all();

    void update(StaffEntity staffEntity);

    void delete(String staffId);

}
