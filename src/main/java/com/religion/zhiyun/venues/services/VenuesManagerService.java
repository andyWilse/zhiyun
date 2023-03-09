package com.religion.zhiyun.venues.services;

import com.religion.zhiyun.utils.response.PcResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.VenuesManagerEntity;

public interface VenuesManagerService {
    /**
     * 新增
     * @param venuesManagerEntity
     */
    RespPageBean add(VenuesManagerEntity venuesManagerEntity, String token);

    /**
     * 修改
     * @param venuesManagerEntity
     */
    void update(VenuesManagerEntity venuesManagerEntity);

    /**
     * 删除
     * @param managerId
     */
    int delete(int managerId);

    /**
     * 查询
     * @return
     */
    PcResponse query();
    /**
     * 查询
     * @return
     */
    PcResponse getManager(String search,String token);


}
