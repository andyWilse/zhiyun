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

    /**
     * 场所新增
     * @param venuesEntity
     * @param token
     * @return
     */
    RespPageBean add(VenuesEntity venuesEntity,String token);

    /**
     * 场所修改
     * @param venuesEntity
     * @param token
     * @return
     */
    RespPageBean update(VenuesEntity venuesEntity,String token);
    int delete(int venuesId,String token);
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

    /**
     * 根据id获取
     * @param venuesId
     * @return
     */
    AppResponse getVenueByID(String venuesId);

    /**
     * 统计场所数量（app首页）
     * @return
     */
    PageResponse getAllNum(String token);
    PageResponse getVenueNum(String type,String token);

    PageResponse getDialogVenue(Map<String, Object> map,String token);

    /**
     * 分页查询
     * @param page
     * @param size
     * @param venuesName
     * @param responsiblePerson
     * @param religiousSect
     * @return
     */
    RespPageBean getVenuesByPage(Integer page, Integer size, String venuesName,
                                 String responsiblePerson, String religiousSect,String venuesPhone,
                                 String token);

    /**
     * 场所下拉(监管)
     * @param map
     * @return
     */
    public AppResponse queryVenues(Map<String, Object> map,String token);

    public AppResponse queryStaffVenues(Map<String, Object> map,String token);

    /**
     * 获取地图详情
     * @param venuesId
     * @return
     */
    AppDetailRes getMapVenuesDetail(String venuesId,String token);

    /**
     * 获取地图
     * @param map
     * @param token
     * @return
     */
    AppResponse getMapVenues(Map<String, Object> map,String token);
    AppResponse getMapsVenues(Map<String, Object> map,String token);
    /**
     * 场所更新用：场所下拉(管理)
     * @param token
     * @param search
     * @return
     */
    AppResponse queryVenuesJz(String token,String search);

    /**
     * 根据分类展示场所列表
     * @param Type
     * @return
     */
    AppResponse getvenuesByType(String Type);

    /**
     * 场所综合得分
     * @return
     */
    AppResponse getVenuesScore();


}
