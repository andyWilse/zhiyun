package com.religion.zhiyun.color.dao;

import com.religion.zhiyun.color.entity.ThreeColorEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ThreeColorMapper {

    /**
     * 新增三色要素
     * @param threeColorEntity
     */
    void addThreeColor(ThreeColorEntity threeColorEntity);

    /**
     * 修改三色要素
     * @param threeColorEntity
     */
    void updateThreeColor(ThreeColorEntity threeColorEntity);

    /**
     * 删除三色要素
     * @param coId
     */
    void deleteThreeColor(@Param("coId") Integer coId,@Param("coModifier") String coModifier);

    /**
     * 获取三色要素场所颜色
     * @param coVenuesId
     * @return
     */
    String getFinalColor(@Param("coVenuesId")String coVenuesId);

    /**
     * 三色要素场所颜色更新
     * @param threeColorEntity
     */
    void updateVenueColor(ThreeColorEntity threeColorEntity);

    /**
     * 获取三色要素
     * @param coId
     * @return
     */
    List<ThreeColorEntity> getThreeColor(@Param("coId") String coId);

    /**
     * 获取场所三色要素
     * @param coVenuesId
     * @return
     */
    List<ThreeColorEntity> getVenueThreeColor(@Param("coVenuesId") Integer coVenuesId);

    /**
     * 获取三色要素列表
     * @param vo
     * @return
     */
    List<ThreeColorEntity> getThreeColorList(@Param("vo") ParamsVo vo);
    Long getThreeColorTotal(@Param("vo") ParamsVo vo);


    /**
     * 天
     * @param vo
     * @return
     */
    List<Map<String,Object>> colorGatherDay(@Param("vo") ParamsVo vo);

    /**
     * 周
     * @param vo
     * @return
     */
    List<Map<String,Object>> colorGatherWeek(@Param("vo") ParamsVo vo);

    /**
     * 月
     * @param vo
     * @return
     */
    List<Map<String,Object>> colorGatherMonth(@Param("vo") ParamsVo vo);

    /**
     * 数量
     * @param vo
     * @return
     */
    List<Map<String,Object>> colorStatistic(@Param("vo") ParamsVo vo);



}
