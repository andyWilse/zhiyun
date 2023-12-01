package com.religion.zhiyun.staff.service;

import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.DetailVo.AppDetailRes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RmStaffInfoService {

    RespPageBean add(StaffEntity staffEntity);

    PageResponse findStaffSelect(Map<String, Object> map);

    RespPageBean update(StaffEntity staffEntity);

    /**
     * 场所除名
     * @param token
     * @return
     */

    PageResponse delete(Map<String, Object> map,String token);

    /**
     * 分页查询
     * @param page
     * @param size
     * @param staffName
     * @param staffVenues
     * @return
     */
    PageResponse getStaffByPage(Integer page, Integer size, String staffName, String staffVenues);

    /**
     * 分页查询全部
     * @param map
     * @param token
     * @return
     */
    PageResponse getAllStaffByPage(Map<String, Object> map, String token);

    Long getMaxStaffCd();

    AppDetailRes getManagerById(String ManagerId);

}
