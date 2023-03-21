package com.religion.zhiyun.venues.services;

import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.PcResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.VenuesManagerEntity;

import java.util.Map;

public interface VenuesManagerService {
    /**
     * 新增
     * @param venuesManagerEntity
     */
    PageResponse add(VenuesManagerEntity venuesManagerEntity, String token);

    /**
     * 修改
     * @param venuesManagerEntity
     * @param token
     * @return
     */
    PageResponse updateManager(VenuesManagerEntity venuesManagerEntity, String token);


    /**
     * 修改
     * @param venuesManagerEntity
     */
    void update(VenuesManagerEntity venuesManagerEntity);

    /**
     * 删除
     * @param managerId
     */
    PageResponse delete(int managerId, String token);

    /**
     * 查询
     * @return
     */
    PcResponse query();
    /**
     * 查询
     * @return
     */
    PageResponse findManager(Map<String, Object> map, String token);

    /**
     * 根据id获取
     * @param managerId
     * @return
     */
    PageResponse getByManagerId(String managerId);
}
