package com.religion.zhiyun.staff.service;

import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.utils.RespPageBean;

import java.util.List;

public interface RmStaffInfoService {

    void add(StaffEntity staffEntity);

    List<StaffEntity> all();

    void update(StaffEntity staffEntity);

    void delete(String staffId);

    /**
     * 分页查询
     * @param page
     * @param size
     * @param staffName
     * @param staffPost
     * @param religiousSect
     * @return
     */
    RespPageBean getStaffByPage(Integer page, Integer size, String staffName, String staffPost, String religiousSect);

    Long getMaxStaffCd();

}
