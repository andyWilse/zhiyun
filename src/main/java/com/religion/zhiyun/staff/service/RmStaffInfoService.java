package com.religion.zhiyun.staff.service;

import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.io.IOException;
import java.util.List;

public interface RmStaffInfoService {

    RespPageBean add(StaffEntity staffEntity);

    List<StaffEntity> all();

    RespPageBean update(StaffEntity staffEntity);

    void delete(int staffId);

    /**
     * 分页查询
     * @param page
     * @param size
     * @param staffName
     * @param staffPost
     * @param religiousSect
     * @return
     */
    RespPageBean getStaffByPage(Integer page, Integer size, String staffName, String staffPost, String religiousSect) throws IOException;

    Long getMaxStaffCd();

    AppResponse getStaffById(String StaffId);

}
