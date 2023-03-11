package com.religion.zhiyun.venues.services;

import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.DetailVo.AppDetailRes;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RmVenuesInfoService {

    RespPageBean add(VenuesEntity venuesEntity,String token);
    RespPageBean update(VenuesEntity venuesEntity);
    int delete(int venuesId);
    VenuesEntity getByResponsiblePerson(String responsiblePerson);

    /**
     * 下拉框查询
     * @param search
     * @param town
     * @return
     */
    RespPageBean querySelect(String search,String town);

    List<VenuesEntity> querySectAll(String religiousSect);
    List<VenuesEntity> getByVenuesFaculty(String venuesName,String responsiblePerson);
    VenuesEntity getVenueByID(String venuesId);

    /**
     * 统计场所数量（app首页）
     * @return
     */
    PageResponse getAllNum(String token);

    /**
     * 分页查询
     * @param page
     * @param size
     * @param venuesName
     * @param responsiblePerson
     * @param religiousSect
     * @return
     */
    RespPageBean getVenuesByPage(Integer page, Integer size, String venuesName, String responsiblePerson, String religiousSect,String token);

    /**
     * 场所下拉
     * @param search
     * @return
     */
    public AppResponse queryVenues(String search);

    /**
     * 获取地图详情
     * @param venuesId
     * @return
     */
    AppDetailRes getMapVenuesDetail(String venuesId);

    /**
     * 获取地图
     * @param search
     * @param religiousSect
     * @return
     */
    AppResponse getMapVenues(String search,String religiousSect);

    /**
     * 场所更新用（教职端）
     * @param token
     * @param search
     * @return
     */
    AppResponse queryVenuesJz(String token,String search);
}
