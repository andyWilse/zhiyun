package com.religion.zhiyun.color.dao;

import com.religion.zhiyun.color.entity.ThreeColorEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * 获取三色要素
     * @param coId
     * @return
     */
    List<ThreeColorEntity> getThreeColor(@Param("coId") String coId);

    /**
     * 获取三色要素列表
     * @param vo
     * @return
     */
    List<ThreeColorEntity> getThreeColorList(@Param("vo") ParamsVo vo);
    Long getThreeColorTotal(@Param("vo") ParamsVo vo);

}
